2026 KITS Hanoi  |  OJT Assignment Specification

## **2026 KITS Hanoi**

# **OJT Assignment Specification**

SW Developer Track  +  IT Interpreter Track

### **0. Assignment Overview**

|**Purpose**|Team project at a practical, industry-level standard that allows company<br>representatives to evaluate and make hiringdecisions based on the finalpresentation|
|-|-|
|**Participating Companies**|Bzcom (SW Developer) / TechValley (SW Developer) / Genie Vina (SW Developer)  /<br>Both Companies(IT Interpreter)|
|**Team Composition**|4-5 membersper team / Developer team and Interpreter team formed separately|
|**Minimum Work Hours**|At least 8 hoursper team(outside of class hours)|
|**Presentation Date**|Graduation Ceremony|
|**Presentation Format**|PPT Presentation|
|**Deliverables**|PPT file + GitHub link(Developers)/ PPT file + Interpretation log (Interpreters)|



#### **Developer Track — Tech Stack**

|**Language**|Java / Python|
|-|-|
|**Architecture**|MVC Pattern / MSA Concepts|
|**API Design**|REST API / Swagger(OpenAPI)|
|**DB Design**|ERD Design / JPA or In-Memory|
|**Collaboration**|Git Flow / Pull Requests / Commit Conventions|
|**AI Utilization**|LLM Integration / Prompt-based Development|



1

2026 KITS Hanoi  |  OJT Assignment Specification

### **PART 1. Bzcom — Developer Team Assignment**

#### **Company 1  Bzcom**

Korea-Vietnam Web/App Development \& Operations Company

#### **Assignment: Customer Request Management System (CRM) REST API Design \& Implementation**

##### **■ Background**

Bzcom is an IT company that operates and maintains web services for 3 Korean client companies. Currently, bug reports, feature requests, and inquiries from clients come in through a mix of email, KakaoTalk, and phone calls. When team members change, the history disappears and missed requests keep recurring. Your team will design and implement a backend system to solve this problem. At the graduation ceremony, a Bizcom representative will evaluate your results directly and decide on hiring.

##### **■ Step 1. ERD Design**

Design an ERD diagram based on the table structure below. Clearly indicate relationships (PK/FK) between tables. Tools such as draw.io or dbdiagram.io are recommended.

```
\[ Tables to Design ]
```

```
1. members
   id, email, password, name,
   role (ADMIN / DEVELOPER / CLIENT), createdAt
```

```
2. requests
   id, title, description,
   category (BUG / FEATURE / INQUIRY),
   priority (HIGH / MEDIUM / LOW),
   status (PENDING / IN\_PROGRESS / DONE),
   clientId → members,  assignedDeveloperId → members,
   createdAt, updatedAt
```

```
3. request\_histories
   id, requestId → requests, changedBy → members,
   fromStatus, toStatus, changedAt, memo
```

```
4. alerts
   id, requestId → requests, targetMemberId → members,
   alertType (ASSIGNED / STATUS\_CHANGED / HIGH\_PRIORITY\_REGISTERED),
   message, isRead, createdAt
```

##### **■ Step 2. REST API Design \& Implementation**

Swagger (OpenAPI) documentation is mandatory for all endpoints. Use a unified response format: { "status": 200, "message": "success", "data": {...} }

* `\[ Auth API ]`

2

2026 KITS Hanoi  |  OJT Assignment Specification

```
POST   /api/auth/login          Login (JWT token issuance)
POST   /api/auth/logout         Logout
\[ Member API ]
POST   /api/members             Register member
GET    /api/members             Get all members (ADMIN only)
GET    /api/members/{id}        Get single member
\[ Request API ]
POST   /api/requests            Register request (CLIENT only)
GET    /api/requests            Get all requests
                                (pagination / sorting / composite filters)
                                ADMIN: all  CLIENT: own only  DEVELOPER: assigned only
GET    /api/requests/{id}       Get single request
PATCH  /api/requests/{id}/assign    Assign developer (ADMIN only / auto-assign logic)
PATCH  /api/requests/{id}/status    Update status (invalid transition prevention)
GET    /api/requests/{id}/history   Get status change history
GET    /api/requests/stats          Statistics (total, completion rate, by category, by
developer)
\[ Alert API ]
GET    /api/alerts              Get own alert list
PATCH  /api/alerts/{id}/read    Mark alert as read
```

```
\[ LLM API — implement 1 of the following ]
POST   /api/requests/classify         Auto-classify category from description
POST   /api/requests/suggest-priority Auto-suggest priority from description
GET    /api/requests/{id}/summary     Auto-generate 1-2 line summary
```

##### **■ Step 3. Core Business Logic**

`1. JWT Authentication \& Authorization`

* `Issue Access Token upon login`
* `Validate token on every API request`
* `Role-based access control:`

```
     ADMIN: full management access
     DEVELOPER: own assigned requests only
     CLIENT: own registered requests only
```

`2. Auto-Assignment Logic`

* `Assign to the DEVELOPER with the lowest currentTaskCount`
* `If tied, assign to the one who completed most recently`

`3. Status Transition Rules`

```
   Allowed:  PENDING → IN\_PROGRESS → DONE
   Blocked:  DONE → reverting to any other status
   Blocked:  PENDING → DONE (skipping IN\_PROGRESS)
```

`4. Automatic History Recording`

* `Auto-record to request\_histories on status change or developer assignment`

`5. Automatic Alert Generation`

* `HIGH priority request registered → alert all ADMINs`
* `Developer assigned → alert that developer`
* `Status changed → alert the requesting CLIENT`

##### **■ Step 4. Git Flow**

3

2026 KITS Hanoi  |  OJT Assignment Specification

```
Branch Strategy
  main              : final release
  develop           : integration branch
  feature/{name}    : individual work branch
Rules
  - Each member works on their own feature branch
  - PR to develop with at least 1 team member review
  - Commit message convention:
    feat:      new feature
    fix:       bug fix
    docs:      documentation
    refactor:  code refactoring
    test:      test code
Deliverable: GitHub link (commit history, branches, PRs included)
```

##### **■ Step 5. PPT Presentation Structure (Graduation Ceremony)**

|**#**|**Slide Title**|**Key Content**|
|-|-|-|
|1|Cover|Team name, member intro, assignment title, companyname|
|2|Problem Definition|Realproblems Bizcom faces + scope of solution|
|3|ERD Design|Table structure + design rationale(whystructured this way)|
|4|API Design Overview|Full endpoint list + RESTful designprinciples applied|
|5|Feature 1 — Auth|JWT flow + role-based access control structure|
|6|Feature 2 — Auto-Assign|**Algorithm explanation + key code walkthrough**|
|7|Feature 3 — History\& Alerts|Status transition rules + auto-recordingflow|
|8|LLM Feature|Chosen feature + Prompt design rationale + actual output|
|9|Swagger Demo|Screenshots of actual API calls|
|10|Git Flow|Branch structure + PR history+ collaboration issues encountered|
|11|Individual Contributions|Each member'spart + keylearnings|
|12|Q\&A||



##### **■ Role Assignment Guide**

|**Member**|**Assigned Part**|
|-|-|
|Member A|Lead ERD design  +  Member \& Auth API implementation|
|Member B|Request register, retrieve, filter,pagination API|
|Member C|Request assign, status change, historyAPI|
|Member D|Alert API  +  common response format  +  exception handling+  Swagger|
|Member E|LLM feature  +  Statistics API  +  Git management  +  PPT lead|



※ ERD, common structure, and Git strategy must be agreed upon by the full team before implementation begins

##### **■ Estimated Time**

|**Phase**|**Content**|
|-|-|
|ERD + API structure alignment|Full team design discussion|



|**Time**|
|-|



**60 min**

4

2026 KITS Hanoi  |  OJT Assignment Specification

|**Phase**|**Content**|**Time**|
|-|-|-|
|Individual implementation|Each member builds assigned APIs|**150 min**|
|Integration + debugging|PR merge + conflict resolution|**80 min**|
|LLM feature completion|Prompt design + implementation|**30 min**|
|PPTproduction|Slide creation + content organization|**80 min**|
|Presentation rehearsal|Full run-through check|**20 min**|
|Total||**approx. 8 hr 20 min**|



##### **■ Hiring Evaluation Criteria**

|**Evaluation Criteria**|**Hire: YES**|**Hire: RECONSIDERING**|
|-|-|-|
|ERD Design|Relationships normalized, rationale clearly<br>explainable|Field listing only, cannot explain<br>relationships|
|REST API|RESTful principles followed, correct HTTP<br>status codes|All handled via POST, verbs in URL|
|JWT Auth|Token flow understood, role-based control<br>implemented|All APIs open with no authentication|
|Business Logic|Auto-assign, transitions, and history work<br>correctly|Logic errors or unhandled exceptions|
|LLM Prompt|Purpose, context, and output format structured<br>clearly|Generic "analyze this" level prompt|
|Git Flow|Branches, PRs, commit conventions actually<br>followed|Direct push to main, meaningless commits|
|PPT Presentation|Design intent and technical decisions explained<br>logically|Reads code only, no explanation of<br>decisions|





