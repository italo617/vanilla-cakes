import { addToCart } from './cartCommons.js';
import { createQuantityComponent } from "./quantityComponent.js";

const cakeInformationElementId = "cake-information";
const cakeNameElementId = "cake-name";
const cakeFigureElementId = "cake-figure";
const cakeDescriptionElementId = "cake-description";
const cakeUnitPriceElementId = "cake-unit-price";
const quantityContainerElementId = "quantity-container";
const totalPriceElementId = "total-price";
const addToCartButtonElementId = "add-to-cart-button";
const errorMessageDivId = "errorMessageDiv";
const errorMessageParagraphId = "errorMessageParagraph";

const cakeImageBaseUrl = "/api/cake-images/by-cake/";

function showError(message) {
    document.getElementById(cakeInformationElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

function updateTotalPrice(quantity, unitPrice) {
    const totalPrice = unitPrice * quantity;
    document.getElementById(totalPriceElementId).textContent = `$ ${totalPrice.toFixed(2)}`;
}

function handleAddToCart(cakeId, quantity) {
    addToCart(cakeId, quantity);
    window.location.href = "cart.html";
}

function createImagePlaceholder() {
    const div = document.createElement("div");

    div.classList.add("cake-image-placeholder", "cake-detail-image");
    div.textContent = "Cake with no image";

    return div;
}

(async function loadCake() {

    const params = new URLSearchParams(window.location.search);

    const cakeIdString = params.get("id");

    const cakeId = Number(cakeIdString);

    if (!cakeIdString || Number.isNaN(cakeId)) {
        showError("Missing valid cake id");
        return;
    }

    try {
        const response = await fetch(`/api/cakes/${cakeId}`);
        if (!response.ok) {
            showError("Could not load cake");
            return;
        }

        const cake = await response.json();

        const cakeImgElement = document.createElement("img");
        cakeImgElement.alt = cake.name;
        cakeImgElement.classList.add("cake-detail-image");
        cakeImgElement.src = `${cakeImageBaseUrl}${cake.id}`;
        cakeImgElement.onerror = () => {
            cakeImgElement.replaceWith(createImagePlaceholder());
        };
        const cakeFigureElement = document.getElementById(cakeFigureElementId);
        cakeFigureElement.appendChild(cakeImgElement);

        document.getElementById(cakeNameElementId).textContent = cake.name;
        document.getElementById(cakeDescriptionElementId).textContent = cake.description;
        document.getElementById(cakeUnitPriceElementId).textContent = `$ ${cake.price.toFixed(2)}`;

        const quantityComponent = createQuantityComponent(1, quantity => {
            updateTotalPrice(quantity, cake.price);
        });
        document.getElementById(quantityContainerElementId).appendChild(quantityComponent.element);

        updateTotalPrice(quantityComponent.getQuantity(), cake.price);

        document.getElementById(addToCartButtonElementId).addEventListener('click', () => {
            handleAddToCart(cakeId, quantityComponent.getQuantity());
        });
    } catch (error) {
        showError("Unexpected error");
        console.error(error);
    }
})();