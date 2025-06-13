# 📝 TechDoc Template for GitHub Commit Documentation
> 📂 *For use by members of the GTS team when updating or committing to a branch.*

---

## 📌 Header Information

- **Branch Name**: `i.e. website`
- **Commit Author**: `Name <email>`
- **Date Committed**: `YYYY-MM-DD`
- **Commit Hash**: `abc123def456...`
- **Related Issue or Task ID**: `#issue-number` (if applicable)

---

## 🧠 Summary of Changes

_A brief and clear summary (3-5 sentences) of what was accomplished in this commit._

Example:  
> Implemented the initial login module using JWT authentication. This includes backend endpoints for login/logout and session validation. The frontend now connects to these endpoints and handles token storage in localStorage.

---

## ⚙️ Technical Details

_Provide a more in-depth explanation of what changed, including:_

- Modules or files modified/created
- Functions/classes added or updated
- Configurations or settings changed
- Technologies or libraries used (and why)
- Any dependencies added

Example:
- Added `authController.js` and `authRoutes.js` in `/backend/controllers/` and `/backend/routes/`
- Introduced JWT for session authentication
- Modified `app.js` to include new authentication middleware
- Frontend `/src/components/Login.vue` added with form validation
- Installed `jsonwebtoken` and `bcryptjs` via npm

---

## 🧪 Testing and Validation

_Explain how the changes were tested. Mention tools used, manual test cases, or any automated test results._

Example:
- ✅ Login with valid credentials returns token and redirects to dashboard
- ❌ Login with invalid credentials shows error toast
- ✅ Token stored and verified using middleware
- ✅ Passed `npm test` with 12 unit tests on authentication routes

---

## 🔗 References & Linked Docs

_Link any related documents, diagrams, papers, or specifications._

- System Flowchart: `/docs/system_flow_v1.png`
- Architecture Spec: [Link to Google Doc or local path]
- Task Board: [Jira/Trello/GitHub Project link]

---

## 🧩 Next Steps / Remaining Tasks

_What needs to be done next based on this commit?_

- [ ] Integrate logout button on dashboard
- [ ] Encrypt passwords before saving
- [ ] Create unit tests for token expiration edge cases

---

## 🗒️ Notes & Considerations

_Any blockers, unusual decisions, known bugs, or design trade-offs made._

Example:  
> Skipped implementing password reset logic until the SMTP server is configured. Using plain text password validation temporarily — **must be secured before deployment.**

---

## 🧾 Document Version Info

- **TechDoc Version**: `v1.0`
- **Reviewed by**: `Name` (if reviewed)
- **Date Reviewed**: `YYYY-MM-DD`
- **Attachments**: `[if applicable, list or link to attachments]`
