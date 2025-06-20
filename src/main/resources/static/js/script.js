document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toggle-btn").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const section = btn.nextElementSibling;
            const isVisible = section.style.display === "block";
            section.style.display = isVisible ? "none" : "block";
            btn.textContent = isVisible ? "▼" : "▲";
        });
    });
});
document.getElementById('syncForm').addEventListener('submit', function() {
    const btn = document.getElementById('syncBtn');
    btn.disabled = true;
    btn.textContent = 'Synchronizing...';
    document.getElementById('loadingIndicator').style.display = 'flex';
});