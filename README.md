# SKD Finance Service — Backend Architecture

Spring Boot + Spring Data JPA + MySQL. Layered: Controller → Service → Repository → Entity,
with DTOs at the controller boundary. Entities, repositories, and DTOs are provided below —
**controllers and services are yours to write**, per your usual workflow.

## Why "Track Application" changed

The reference design implies live status pulled from a bank's system. That's not something
a consultancy site can do — banks don't expose that to third parties. What actually works:
your advisors update a case's `currentStage` by hand as it moves through the process
(`Documents Submitted` → `Under Review` → `Approved` → `Disbursed`), and the client looks
up their `referenceNumber` on a public page to see that internal status. This is modeled as
`ApplicationCase` + `CaseStatusUpdate` below.

## Domain areas and what each service layer must handle

**LeadService** (contact form / "enquire now")
- Validate `LeadRequestDTO` with `@Valid` in the controller; return 400 with field errors on failure.
- Default `status` to `"NEW"` and `createdAt` to `LocalDateTime.now()` before saving — don't trust the client for these.
- Since `status` is a plain String (no enums here), whitelist allowed values in the service before any update — reject anything outside `NEW / CONTACTED / CONVERTED / CLOSED` with a custom exception mapped to 400.

**EligibilityService** (EMI-adjacent but server-computed, unlike the calculator which stays client-side)
- Validate the request (income > 0, age 18–75, CIBIL 300–900 if provided) — the DTO annotations handle the shape, but business rules (e.g. "existing EMI can't exceed monthly income") belong in the service.
- Compute `eligibleAmount` and `eligibilityScore` from income, existing EMI, and CIBIL — this is your business formula to define; keep it in one method so it's easy to tune later.
- Persist the assessment regardless of outcome (good or poor eligibility) — even "not eligible" is a lead.

**ApplicationCaseService** (internal case tracking)
- `findByReferenceNumber` should throw a `CaseNotFoundException` → mapped to 404 — never leak whether a reference number "almost" matched.
- Reference numbers should be generated server-side (e.g. `SKD-2026-00482`), never accepted from the client, to prevent someone guessing/creating fake cases.
- Adding a `CaseStatusUpdate` must also update the parent `ApplicationCase.currentStage` and `updatedAt` in the same transaction — use `@Transactional` so they can't drift apart.

**BlogPostService**
- Slug must be unique — check `existsBySlug` equivalent before insert/update and throw a clear exception (409) rather than letting the DB constraint fail ugly.
- `publishedAt` in the future should probably exclude a post from public listings — that's a query-time filter (`publishedAt <= today`), not a delete.

**TestimonialService**
- New testimonials should default `approved = false` — an advisor flips it on. Never expose an "approve" endpoint publicly without auth.

**ServiceOfferingService**
- Straightforward CRUD, ordered by `displayOrder`. Low risk, low validation needs — good one to build first.

## Suggested endpoints (for your controllers)

```
GET    /api/services                        list all, ordered
GET    /api/services/{slug}                  one service detail

GET    /api/blog                             list published posts (summary DTO)
GET    /api/blog/{slug}                      one full post

GET    /api/testimonials                     approved only

POST   /api/leads                            public contact form
POST   /api/eligibility/check                public eligibility checker

GET    /api/cases/{referenceNumber}          public status lookup

# admin-only — needs auth before these go live (see note below)
GET    /api/admin/leads
PATCH  /api/admin/leads/{id}/status
POST   /api/admin/cases
POST   /api/admin/cases/{id}/status-updates
POST   /api/admin/blog
PUT    /api/admin/blog/{id}
POST   /api/admin/testimonials
PATCH  /api/admin/testimonials/{id}/approve
```

## Security — not included yet, on purpose

None of this has auth wired in, matching how we've built things so far. But the `/api/admin/*`
group above genuinely needs it before you deploy — anyone could otherwise edit case statuses,
publish blog posts, or read every lead. When you're ready, that's Spring Security + JWT (or
session-based, your call) scoped to those admin paths only; the public endpoints stay open.
Flag it back to me when you want to build that part.

## Folder structure

```
src/main/java/com/skdfinance/backend/
├── entity/          (7 files — provided)
├── repository/      (7 files — provided)
├── dto/             (10 files — provided)
├── controller/      (yours to write)
├── service/         (yours to write)
└── exception/       (yours — custom exceptions + @ControllerAdvice)
```

---

## Status: complete for MVP scope

All six services and controllers are implemented, following the pattern above exactly:
`ServiceOfferingService`, `TestimonialService`, `BlogPostService`, `LeadService`,
`EligibilityService`, and `ApplicationCaseService` — each with its matching controller.

`GlobalExceptionHandler` (in `exception/`) catches everything they throw:
- `MethodArgumentNotValidException` (from `@Valid`) → 400, with a `fieldErrors` map
- `IllegalArgumentException` → 400
- `NoSuchElementException` → 404
- `IllegalStateException` → 409
- anything else → 500, with a generic message (real error stays in your server logs)

Two DTOs were added beyond the original list, both admin-only:
`ApplicationCaseCreateDTO` (has phone/email — never exposed on the public-facing
`ApplicationCaseDTO`) and `CaseStatusUpdateRequestDTO`.

### Not yet done, on purpose
- **Security** — every `/api/admin/*` endpoint is wide open right now. Do not deploy this
  as-is; add Spring Security (JWT or session-based) scoped to those paths before going live.
- **EligibilityService's rate/tenure assumptions and score weights** are reasonable starting
  points, not underwriting-grade numbers — revisit once real approval data exists.
- **Reference number generation** uses a random-plus-retry check, fine at this scale; move to
  a DB sequence if case volume grows.
