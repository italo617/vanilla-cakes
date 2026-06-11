export function createQuantityComponent(initialQuantity = 1, onQuantityChanged = () => {}) {

    const container = document.createElement("div");

    const decreaseButton = document.createElement("button");
    decreaseButton.textContent = "-";

    const quantityInput = document.createElement("input");
    quantityInput.type = "text";
    quantityInput.value = initialQuantity;
    quantityInput.readOnly = true;
    quantityInput.style.pointerEvents = "none";

    const increaseButton = document.createElement("button");
    increaseButton.textContent = "+";

    decreaseButton.addEventListener("click", () => {
        let quantity = Number(quantityInput.value);

        if (quantity > 1) {
            quantity -= 1;
            quantityInput.value = String(quantity);
            onQuantityChanged(quantity);
        }
    });

    increaseButton.addEventListener("click", () => {
        const quantity = Number(quantityInput.value) + 1;
        quantityInput.value = String(quantity);
        onQuantityChanged(quantity);
    });

    container.appendChild(decreaseButton);
    container.appendChild(quantityInput);
    container.appendChild(increaseButton);

    return {
        element: container,
        getQuantity: () => Number(quantityInput.value)
    };
}