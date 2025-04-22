  
document.addEventListener("DOMContentLoaded", () => {
    let currentPage = 1; // Spring starts 0
    const pageSize = 10;

    document.getElementById("loadMoreBtn").addEventListener("click", async () => {
        const res = await fetch(`/api/products/?page=${currentPage}&size=${pageSize}`);
        const data = await res.json();

        const container = document.querySelector(".product-container");

        data.products.forEach(product => {
            const a = document.createElement("a");
            a.href = `/products/${product.id}`;
            a.className = "product-item";
            a.innerHTML = `
                <div class="product-item-content">
                    <img class="product-img" src="/products/${product.id}/image" alt="${product.name}">
                    <p class="product-name">${product.name}</p>
                    <p class="product-price">${product.price} €</p>
                </div>
            `;
            container.appendChild(a);
        });

        currentPage++;

        if (currentPage >= data.totalPages) {
            document.getElementById("loadMoreBtn").style.display = "none";
        }
    });
});



