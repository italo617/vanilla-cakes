const cartStorageKey = "cart";

//Structure:
//  key is the cakeId, and value is the quantity in the cart related to that cakeId

function loadCart() {
    try {
        const cart = JSON.parse(localStorage.getItem(cartStorageKey) ?? "{}");
        const sanitizedCart = {};

        for (const [cakeId, quantity] of Object.entries(cart)) {
            const parsedCakeId = Number(cakeId);
            if (!Number.isInteger(parsedCakeId) || parsedCakeId <= 0) {
                continue;
            }
            if (!Number.isInteger(quantity) || quantity <= 0) {
                continue;
            }
            sanitizedCart[parsedCakeId] = quantity;
        }
        return sanitizedCart;
    } catch {
        return {};
    }
}

function saveCart(cart) {
    localStorage.setItem(cartStorageKey, JSON.stringify(cart));
}

function addToCart(cakeId, quantity) {
    const cart = loadCart();
    if (!Number.isInteger(quantity) || quantity <= 0) {
        return;
    }
    cart[cakeId] = (cart[cakeId] || 0) + quantity;
    saveCart(cart);
}

function setToCart(cakeId, quantity) {
    const cart = loadCart();
    if (!Number.isInteger(quantity) || quantity <= 0) {
        removeFromCart(cakeId);
        return;
    }
    cart[cakeId] = quantity;
    saveCart(cart);
}

function removeFromCart(cakeId) {
    const cart = loadCart();
    delete cart[cakeId];
    saveCart(cart);
}

function clearCart() {
    saveCart({});
}

export {
    loadCart,
    addToCart,
    setToCart,
    removeFromCart,
    clearCart
}