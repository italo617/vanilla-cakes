const cakesContainerElementId = "cakesContainer";
const paginationElementId = "pagination";
const errorMessageDivId = "errorMessageDiv";
const errorMessageParagraphId = "errorMessageParagraph";

function showError(message) {
    document.getElementById(cakesContainerElementId).innerHTML = "";
    document.getElementById(cakesContainerElementId).hidden = true;
    document.getElementById(paginationElementId).hidden = true;
    document.getElementById(errorMessageDivId).hidden = false;
    document.getElementById(errorMessageParagraphId).textContent = message;
}

function createCakeElement(cake) {
    const cakeNameElement = document.createElement("h2");
    cakeNameElement.textContent = cake.name;

    const cakePriceElement = document.createElement("p");

    cakePriceElement.textContent = `$ ${cake.price.toFixed(2)}`;
    const cakeLinkElement = document.createElement("a");
    cakeLinkElement.href = `/cakeDetail.html?id=${cake.id}`;
    cakeLinkElement.textContent = "View details";

    const cakeElement = document.createElement("div");
    cakeElement.style = "border: 1px solid black";
    cakeElement.appendChild(cakeNameElement);
    cakeElement.appendChild(cakePriceElement);
    cakeElement.appendChild(cakeLinkElement);

    return cakeElement;
}

//TODO: Improve pagination design
function renderPagination(pagedResponse) {

    const paginationElement = document.getElementById(paginationElementId);

    paginationElement.innerHTML = "";

    const currentPage = pagedResponse.page;
    const totalPages = pagedResponse.totalPages;

    if (totalPages <= 1) {
        return;
    }

    function createPageButton(page, text = page) {

        const button = document.createElement("button");

        button.textContent = text;

        if (page === currentPage) {
            button.disabled = true;
            button.style.fontWeight = "bold";
        }

        button.addEventListener("click", () => {
            loadCakes(page);
        });

        return button;
    }

    function appendEllipsis() {

        const span = document.createElement("span");

        span.textContent = "...";

        paginationElement.appendChild(span);
    }

    // Navigation to first and previous pages
    if (currentPage > 1) {

        paginationElement.appendChild(
            createPageButton(1, "<<")
        );

        paginationElement.appendChild(
            createPageButton(currentPage - 1, "<")
        );
    }

    const startPage =
        Math.max(1, currentPage - 2);

    const endPage =
        Math.min(totalPages, currentPage + 2);

    // Left ellipsis
    if (startPage > 1) {

        paginationElement.appendChild(
            createPageButton(1)
        );

        if (startPage > 2) {
            appendEllipsis();
        }
    }

    // Main pages
    for (let page = startPage;
         page <= endPage;
         page++) {

        paginationElement.appendChild(
            createPageButton(page)
        );
    }

    // Right ellipsis
    if (endPage < totalPages) {

        if (endPage < totalPages - 1) {
            appendEllipsis();
        }

        paginationElement.appendChild(
            createPageButton(totalPages)
        );
    }

    // Navigation to next and last pages
    if (currentPage < totalPages) {

        paginationElement.appendChild(
            createPageButton(currentPage + 1, ">")
        );

        paginationElement.appendChild(
            createPageButton(totalPages, ">>")
        );
    }
}

async function loadCakes(page = 1) {
    document.getElementById(cakesContainerElementId).hidden = false;
    document.getElementById(paginationElementId).hidden = false;
    document.getElementById(errorMessageDivId).hidden = true;

    try {
        const response = await fetch(`/api/cakes?page=${page}`);

        if (!response.ok) {
            showError("Could not load cakes");
            return;
        }

        const cakePage = await response.json();

        const cakesContainer = document.getElementById(cakesContainerElementId);
        cakesContainer.innerHTML = ""; // Prevents old cakes from remaining after reload
        for (const cake of cakePage.content) {
            const cakeElement = createCakeElement(cake);
            cakesContainer.appendChild(cakeElement);
        }
        renderPagination(cakePage);

    } catch (error) {
        showError("Unexpected error");
        console.error(error);
    }
}

(async function initialize() {
    await loadCakes();
})();