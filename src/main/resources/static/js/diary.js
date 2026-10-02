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