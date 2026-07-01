const orderSuccessContentElementId = "order-success-content";
const orderIdElementId = "order-id";
const orderCreatedAtElementId = "order-created-at";
const errorMessageDivId = "errorMessageDiv";
const errorMessageParagraphId = "errorMessageParagraph";

function showError(message) {
    document.getElementById(orderSuccessContentElementId).innerHTML = "";
    document.getElementById(orderSuccessContentElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

function loadOrderData() {
    const lastOrder = JSON.parse(sessionStorage.getItem("last-order"));
    if (!lastOrder) {
        showError("Unexpected error. Last order not found.");
        return;
    }

    const orderIdElement = document.getElementById(orderIdElementId);
    orderIdElement.innerText = lastOrder.id;

    const orderCreatedAtElement = document.getElementById(orderCreatedAtElementId);
    const formatter = new Intl.DateTimeFormat('en-US', {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "numeric",
        minute: "numeric"
    });
    orderCreatedAtElement.innerText = formatter.format(new Date(lastOrder.createdAt));

    sessionStorage.removeItem("last-order");
}

loadOrderData();