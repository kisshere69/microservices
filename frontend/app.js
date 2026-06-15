const ticketStatuses = [
  "NEW",
  "OPEN",
  "INVESTIGATING",
  "WAITING_FOR_CUSTOMER",
  "RESOLVED",
  "CLOSED",
];

const transitions = {
  NEW: ["OPEN"],
  OPEN: ["INVESTIGATING", "CLOSED"],
  INVESTIGATING: ["WAITING_FOR_CUSTOMER", "RESOLVED"],
  WAITING_FOR_CUSTOMER: ["INVESTIGATING", "CLOSED"],
  RESOLVED: ["CLOSED", "OPEN"],
  CLOSED: [],
};

const elements = {
  apiBaseUrl: document.querySelector("#apiBaseUrl"),
  createTicketForm: document.querySelector("#createTicketForm"),
  loadTicketForm: document.querySelector("#loadTicketForm"),
  ticketTitle: document.querySelector("#ticketTitle"),
  customerId: document.querySelector("#customerId"),
  ticketId: document.querySelector("#ticketId"),
  ticketDetails: document.querySelector("#ticketDetails"),
  statusControls: document.querySelector("#statusControls"),
  message: document.querySelector("#message"),
};

let currentTicket = null;

elements.apiBaseUrl.value =
  window.localStorage.getItem("ticketApiBaseUrl") || elements.apiBaseUrl.value;

elements.apiBaseUrl.addEventListener("change", () => {
  window.localStorage.setItem("ticketApiBaseUrl", getApiBaseUrl());
});

elements.createTicketForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  await runRequest(async () => {
    const ticket = await createTicket({
      title: elements.ticketTitle.value.trim(),
      customerId: elements.customerId.value.trim(),
    });

    elements.ticketId.value = ticket.id;
    elements.createTicketForm.reset();
    setCurrentTicket(ticket);
    showMessage("Ticket created.", "success");
  });
});

elements.loadTicketForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  await runRequest(async () => {
    const ticket = await getTicket(elements.ticketId.value.trim());
    setCurrentTicket(ticket);
    showMessage("Ticket loaded.", "success");
  });
});

function getApiBaseUrl() {
  return elements.apiBaseUrl.value.trim().replace(/\/$/, "");
}

async function createTicket(input) {
  return request("/tickets", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

async function getTicket(id) {
  return request(`/tickets/${encodeURIComponent(id)}`);
}

async function updateTicketStatus(id, status) {
  return request(`/tickets/${encodeURIComponent(id)}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });
}

async function request(path, options = {}) {
  const response = await fetch(`${getApiBaseUrl()}${path}`, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error(await readError(response));
  }

  return response.json();
}

async function readError(response) {
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    const body = await response.json();
    return body.detail || body.message || body.error || response.statusText;
  }

  const text = await response.text();
  return text || response.statusText;
}

async function runRequest(callback) {
  setButtonsDisabled(true);
  clearMessage();

  try {
    await callback();
  } catch (error) {
    showMessage(error.message || "Request failed.", "error");
  } finally {
    setButtonsDisabled(false);
  }
}

function setCurrentTicket(ticket) {
  currentTicket = ticket;
  renderTicket(ticket);
  renderStatusControls(ticket);
}

function renderTicket(ticket) {
  elements.ticketDetails.classList.remove("empty");
  elements.ticketDetails.innerHTML = `
    <div class="panel-header">
      <div>
        <h2>${escapeHtml(ticket.title)}</h2>
        <p>${escapeHtml(ticket.id)}</p>
      </div>
      <span class="status-badge">${escapeHtml(ticket.status)}</span>
    </div>
    <div class="ticket-grid">
      ${renderTicketField("Customer", ticket.customerId)}
      ${renderTicketField("Created", formatDate(ticket.createdAt))}
      ${renderTicketField("Status", ticket.status)}
      ${renderTicketField("Ticket ID", ticket.id)}
    </div>
  `;
}

function renderTicketField(label, value) {
  return `
    <div class="ticket-field">
      <strong>${escapeHtml(label)}</strong>
      <span>${escapeHtml(value)}</span>
    </div>
  `;
}

function renderStatusControls(ticket) {
  const allowed = new Set(transitions[ticket.status] || []);

  elements.statusControls.innerHTML = ticketStatuses
    .filter((status) => status !== ticket.status)
    .map((status) => {
      const disabled = !allowed.has(status);
      const title = disabled
        ? `Cannot move from ${ticket.status} to ${status}`
        : `Move to ${status}`;
      return `
        <button
          type="button"
          data-status="${status}"
          title="${title}"
          ${disabled ? 'data-transition-blocked="true" disabled' : ""}
        >
          ${status}
        </button>
      `;
    })
    .join("");

  elements.statusControls.querySelectorAll("button[data-status]").forEach((button) => {
    button.addEventListener("click", async () => {
      await runRequest(async () => {
        const updatedTicket = await updateTicketStatus(currentTicket.id, button.dataset.status);
        setCurrentTicket(updatedTicket);
        showMessage(`Status changed to ${updatedTicket.status}.`, "success");
      });
    });
  });
}

function formatDate(value) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function showMessage(text, type) {
  elements.message.textContent = text;
  elements.message.className = `message visible ${type}`;
}

function clearMessage() {
  elements.message.textContent = "";
  elements.message.className = "message";
}

function setButtonsDisabled(disabled) {
  document.querySelectorAll("button").forEach((button) => {
    button.disabled = disabled || button.dataset.transitionBlocked === "true";
  });
}
