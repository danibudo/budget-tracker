> Note: this README.md file has been generated with Claude Code.
# budget-tracker

Personal budget tracking application — a portfolio project demonstrating a full-stack Java/React setup with a Jakarta EE REST API and a single-page React frontend.

**Live demo: [budgetorwhat.netlify.app](https://budgetorwhat.netlify.app)**

## Overview

This is a monorepo containing two components:

- **`back-end/`** — a Jakarta EE 10 REST API deployed on WildFly, backed by a MySQL database. It manages transactions and categories, and exposes aggregated summary data used by the dashboard.
- **`budget-ui/`** — a React single-page application that consumes the API. It provides a tabbed interface for the dashboard, transaction management, and category management.

The application is intentionally stateless from a demo perspective: **data is wiped and re-seeded with mock transactions on every startup and every 24 hours.** This lets visitors explore and experiment freely without breaking the shared demo state. Persistent storage is straightforward to enable by removing the `DataSeederStartup` bean.

## Features

- **Dashboard** — summary cards showing current balance, total income, and total expenses; an expense breakdown pie chart by category; and a monthly income vs. expenses bar chart
- **Transaction management** — add income and expense transactions linked to a category; browse and filter by date range; delete individual transactions
- **Category management** — create categories with a name, type (`INCOME` or `EXPENSE`), and a display color; edit and delete categories
- **Daily data reset** — on startup and every 24 hours, the database is wiped and re-populated with realistic mock data spanning three months

## Tech Stack

### API (`back-end/`)

- **Runtime:** Java 17
- **Framework:** Jakarta EE 10 (JAX-RS, CDI, JPA)
- **Server:** WildFly 33 (`standalone-microprofile.xml`)
- **Database:** MySQL 8 (Hibernate/JPA, schema auto-generated via `jakarta.persistence.schema-generation`)
- **Build:** Maven, packaged as a WAR
- **Containerisation:** Docker (multi-stage build — Maven builder + WildFly runtime, non-root user)

### UI (`budget-ui/`)

- **Runtime:** React 19
- **Bundler:** Vite
- **UI library:** Mantine v9
- **Charts:** Recharts
- **Build:** `npm run build` → static assets in `dist/`

## API

No authentication is required. All endpoints return and accept `application/json`.

| Method | Path | Query params | Description |
|---|---|---|---|
| `GET` | `/api/transactions` | `from`, `to` (dates, optional) | List transactions, optionally filtered by date range |
| `GET` | `/api/transactions/{id}` | — | Get a transaction by ID |
| `POST` | `/api/transactions` | — | Create a transaction |
| `PUT` | `/api/transactions/{id}` | — | Update a transaction |
| `DELETE` | `/api/transactions/{id}` | — | Delete a transaction |
| `GET` | `/api/categories` | `type` (`INCOME`\|`EXPENSE`, optional) | List categories, optionally filtered by type |
| `GET` | `/api/categories/{id}` | — | Get a category by ID |
| `POST` | `/api/categories` | — | Create a category |
| `PUT` | `/api/categories/{id}` | — | Update a category |
| `DELETE` | `/api/categories/{id}` | — | Delete a category |
| `GET` | `/api/summary` | — | Total balance, income, and expenses |
| `GET` | `/api/summary/expenses-by-category` | — | Expense totals grouped by category |
| `GET` | `/api/summary/monthly` | — | Monthly income and expense totals |

**Create transaction**
```
POST /api/transactions
Content-Type: application/json

{
  "amount": "42.50",
  "description": "Supermarket",
  "date": "2025-05-14",
  "categoryId": 3
}
```

**Create category**
```
POST /api/categories
Content-Type: application/json

{
  "name": "Groceries",
  "type": "EXPENSE",
  "color": "#f08c00"
}
```

Error responses use a structured body with a `code` field matching one of the defined error codes (`CATEGORY_NOT_FOUND`, `CATEGORY_DUPLICATE`, `TRANSACTION_NOT_FOUND`, `INVALID_AMOUNT`, `INVALID_DATE_RANGE`, `INVALID_TYPE`).

## Running Locally

### API

The API requires a running MySQL instance. The quickest way is Docker:

```bash
docker run --rm -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=budget_tracker \
  mysql:8
```

Then build and run the API container from the `back-end/` directory:

```bash
cd back-end
docker build -t budget-tracker-api .
docker run --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=budget_tracker \
  -e DB_USER=root \
  -e DB_PASSWORD=root \
  budget-tracker-api
```

The API will be available at `http://localhost:8080/budget-tracker-api/api/` (default WildFly context root derived from the WAR filename).

### UI

From the `budget-ui/` directory:

```bash
npm install
npm run dev
```

The Vite dev server starts on `http://localhost:5173`. The proxy in `vite.config.js` automatically forwards `/api` requests to `http://localhost:8080/budget-tracker-api/api`, so no additional configuration is needed.

## Environment Variables

These are required by the API at runtime:

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | — | MySQL hostname |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | — | Database name |
| `DB_USER` | — | Database username |
| `DB_PASSWORD` | — | Database password |

## Deployment

Stack: WildFly API on **Fly.io** · MySQL database on **Aiven** · React frontend on **Netlify**

### Database — Aiven

The MySQL database is hosted on [Aiven](https://aiven.io). Create a MySQL service (any region close to `ams`), then note the host, port, database name, user, and password from the service overview. Aiven requires SSL for all connections; the WildFly datasource is configured with `sslMode=REQUIRED`.

Because Fly.io machines use dynamic IPs, the Aiven service's allowed IP list must include `0.0.0.0/0`.

### API — Fly.io

The API is deployed on [Fly.io](https://fly.io) from the `back-end/` directory. The `fly.toml` targets the `ams` region on a `shared-cpu-1x` / 1 GB machine. The `jboss-web.xml` overrides the WildFly context root to `/` in the production image, so the base path on Fly.io is simply `/api/`.

```bash
cd back-end
fly deploy
```

The five `DB_*` environment variables must be set as Fly secrets (using the Aiven connection details) before the first deploy:

```bash
fly secrets set DB_HOST=<aiven-host> DB_PORT=<aiven-port> DB_NAME=<db-name> DB_USER=<user> DB_PASSWORD=<password>
```

### UI — Netlify

The frontend is deployed on [Netlify](https://netlify.com). The `netlify.toml` runs `npm run build` and publishes the `dist/` directory. A redirect rule proxies all `/api/*` requests to the Fly.io backend, so the built frontend never needs the backend URL baked in:

```toml
[[redirects]]
  from = "/api/*"
  to   = "https://budget-tracker-api.fly.dev/api/:splat"
  status = 200
  force  = true
```

Netlify auto-deploys on every push to the main branch. No environment variables are required for the frontend build.
