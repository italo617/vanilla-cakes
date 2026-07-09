const orderSuccessContentElementId = "order-success-content";
const orderIdElementId = "order-id";
const orderCreatedAtElementId = "order-created-at";
const orderClientNameElementId = "order-client-name";
const orderFullAddressElementId = "order-full-address";
const orderPaymentMethodElementId = "order-payment-method";
const paymentMethodLabels = {
    "cash_on_delivery": "Cash On Delivery",
    "credit_card": "Credit Card",
    "debit_card": "Debit Card"
}
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
    orderIdElement.textContent = lastOrder.id;

    const orderCreatedAtElement = document.getElementById(orderCreatedAtElementId);
    const formatter = new Intl.DateTimeFormat('en-US', {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "numeric",
        minute: "numeric"
    });
    orderCreatedAtElement.textContent = formatter.format(new Date(lastOrder.createdAt));

    const orderClientNameElement = document.getElementById(orderClientNameElementId);
    orderClientNameElement.textContent = lastOrder.clientName;

    const orderFullAddressElement = document.getElementById(orderFullAddressElementId);
    orderFullAddressElement.textContent = lastOrder.fullAddress;

    const orderPaymentMethodElement = document.getElementById(orderPaymentMethodElementId);
    orderPaymentMethodElement.textContent = paymentMethodLabels[lastOrder.paymentMethod] ?? lastOrder.paymentMethod;

    sessionStorage.removeItem("last-order");
}

loadOrderData();