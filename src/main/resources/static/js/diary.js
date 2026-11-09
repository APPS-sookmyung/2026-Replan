const diaryButton = document.getElementById("diaryButton");

diaryButton.addEventListener("click", function () {
    if (diaryButton.disabled) return;

    const content = document.getElementById("diaryContent").value;
    const originalText = diaryButton.textContent;

    diaryButton.disabled = true;
    diaryButton.textContent = "키워드 생성 중...";
    diaryButton.setAttribute("aria-busy", "true");

    apiFetch("/diary?content=" + encodeURIComponent(content), {
        method: "POST"
    })
        .then(function (response) {
            if (!response.ok) throw new Error("일기 저장에 실패했어요.");
            return apiFetch("/diary/analyze?content=" + encodeURIComponent(content), {
                method: "POST"
            });
        })
        .then(function (response) {
            if (!response.ok) throw new Error("키워드 생성에 실패했어요.");
            return response.text();
        })
        .then(function (keywords) {
            document.getElementById("keywordResult").textContent = keywords;
        })
        .catch(function (error) {
            alert("요청을 완료하지 못했어요. " + error.message);
        })
        .finally(function () {
            diaryButton.disabled = false;
            diaryButton.textContent = originalText;
            diaryButton.removeAttribute("aria-busy");
        });
});
