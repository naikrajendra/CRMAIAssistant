# CRMAIAssistant

CRMAIAssistant is a Spring Boot + SpringAI application that reads a batch of CRM accounts, summarizes recent changes such as leads, quotes, opportunities, orders, and support items, and creates a daily briefing email for each account.

## Features

- Reads 50 sample CRM accounts
- Tracks change events across leads, quotes, opportunities, orders, and cases
- Uses SpringAI to draft a customer-facing daily briefing email
- Sends the briefing via JavaMail
- Exposes health and metrics endpoints for observability
- Records confidence scores and token usage in Micrometer

## C4 model diagrams

```mermaid
C4Context
    title CRM AI Assistant - System Context

    Person(accountOwner, "Account Owner", "Receives daily email briefings for key accounts")
    Person(csm, "Customer Success Manager", "Reviews account health and risks")
    System(crmAi, "CRM AI Assistant", "Reads CRM account data, summarizes changes, scores confidence, and emails daily briefings")
    System_Ext(crm, "CRM Platform", "Sales, quote, opportunity, order, and case data")
    System_Ext(openAi, "OpenAI API", "Generates human-readable briefing text")
    System_Ext(smtp, "SMTP Server", "Delivers daily emails")

    Rel(accountOwner, crmAi, "Receives daily briefing emails")
    Rel(csm, crmAi, "Reviews account assessments and trends")
    Rel(crmAi, crm, "Reads account activity and change events")
    Rel(crmAi, openAi, "Generates account brief content")
    Rel(crmAi, smtp, "Sends briefing emails")
```

```mermaid
C4Container
    title CRM AI Assistant - Container Diagram

    System_Boundary(crmSystem, "CRM AI Assistant") {
        Container(web, "Spring Boot App", "Java 21 / Spring Boot", "Serves APIs, schedules daily jobs, and coordinates briefing generation")
        Container(db, "In-Memory CRM Dataset", "Java Objects / Runtime State", "Contains sample account and change records")
        Container(metrics, "Observability Stack", "Micrometer / Actuator", "Collects confidence score and token metrics")
    }

    System_Ext(crm, "CRM Platform", "Source of account data and change events")
    System_Ext(openAi, "OpenAI API", "AI generation")
    System_Ext(smtp, "SMTP Server", "Email delivery")

    Rel(web, crm, "Loads account records and change history")
    Rel(web, openAi, "Calls chat model for drafting")
    Rel(web, smtp, "Dispatches daily briefing emails")
    Rel(web, db, "Reads account data snapshot")
    Rel(web, metrics, "Publishes briefing and usage metrics")
```

```mermaid
C4Component
    title CRM AI Assistant - Component Diagram

    Component(app, "CrmAiAssistantApplication", "Spring Boot", "Bootstraps the application and scheduling")
    Component(scheduler, "DailyBriefingScheduler", "Scheduling", "Runs daily briefing generation at 4:00 AM")
    Component(accountSvc, "CrmAccountDataService", "Service", "Loads CRM account and change records")
    Component(briefingSvc, "DailyBriefingService", "Service", "Builds prompts and orchestrates brief generation")
    Component(evalSvc, "BriefingEvaluationService", "Service", "Calculates confidence score and token estimates")
    Component(emailSvc, "EmailDispatchService", "Service", "Sends formatted HTML emails")
    Component(templateSvc, "EmailTemplateService", "Service", "Renders branded briefing email bodies")
    Component(obsSvc, "ObservabilityService", "Service", "Publishes custom Micrometer metrics")
    Component(chat, "ChatClient", "Spring AI", "Invokes the LLM for draft generation")

    Rel(app, scheduler, "Enables")
    Rel(scheduler, briefingSvc, "Triggers daily processing")
    Rel(briefingSvc, accountSvc, "Loads account data")
    Rel(briefingSvc, chat, "Requests briefing draft")
    Rel(briefingSvc, evalSvc, "Calculates confidence and token usage")
    Rel(briefingSvc, emailSvc, "Passes briefing content for delivery")
    Rel(emailSvc, templateSvc, "Uses branded HTML template")
    Rel(briefingSvc, obsSvc, "Records generation and telemetry")
```

## Run locally

```bash
mvn spring-boot:run
```

Then open:

- http://localhost:8081/api/health
- http://localhost:8081/api/accounts
- http://localhost:8081/api/briefings/generate
- http://localhost:8081/actuator/metrics

## Configure AI and email

```bash
export OPENAI_API_KEY=your-key
export OPENAI_MODEL=gpt-4o-mini
export SMTP_HOST=localhost
export SMTP_PORT=1025
```

## Observability

The app exposes Micrometer metrics including:

- `crm.ai.briefings.generated`
- `crm.ai.confidence.score`
- `crm.ai.token.usage`

These are visible through the Actuator endpoints.
