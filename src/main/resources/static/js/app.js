const diaryButton = document.getElementById("diaryButton");

diaryButton.addEventListener("click", function () {
    const content = document.getElementById("diaryContent").value;

    fetch("/diary?content=" + encodeURIComponent(content), {
        method: "POST"
    })
        .then(function () {
            return fetch("/diary/analyze?content=" + encodeURIComponent(content), {
                method: "POST"
            });
        })
        .then(function (response) {
            return response.text();
        })
        .then(function (keywords) {
            document.getElementById("keywordResult").textContent = keywords;
        });
});

const todoButton = document.getElementById("todoButton");

todoButton.addEventListener("click", function () {
    const title = document.getElementById("todoTitle").value;
    const deadline = document.getElementById("todoDeadline").value;

    fetch("/todo?title=" + encodeURIComponent(title)
        + "&deadline=" + encodeURIComponent(deadline), {
        method: "POST"
    })
        .then(function () {
            return fetch("/todo/plan?title=" + encodeURIComponent(title)
                + "&deadline=" + encodeURIComponent(deadline), {
                method: "POST"
            });
        })
        .then(function (response) {
            return response.text();
        })
        .then(function (plan) {
            document.getElementById("planResult").textContent = plan;
        });
});