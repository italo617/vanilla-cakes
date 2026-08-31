async function loadComponent(elementId, componentPath) {
    const element = document.getElementById(elementId);

    const response = await fetch(componentPath);

    if (!response.ok) {
        throw new Error(`Could not load component: ${componentPath}`);
    }

    element.innerHTML = await response.text();
}

async function loadPageComponents() {
    await Promise.all([
        loadComponent("site-header", "./components/header.html"),
        loadComponent("site-footer", "./components/footer.html")
    ]);
}

loadPageComponents().catch(console.error);