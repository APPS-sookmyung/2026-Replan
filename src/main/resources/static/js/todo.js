const todoButton = document.getElementById("todoButton");

todoButton.addEventListener("click", function () {
    if (todoButton.disabled) return;

    const title = document.getElementById("todoTitle").value;
    const additionalInfo =
        document.getElementById("todoAdditionalInfo").value;
    const deadline = document.getElementById("todoDeadline").value;
    const originalText = todoButton.textContent;

    todoButton.disabled = true;
    todoButton.textContent = "계획 생성 중...";
    todoButton.setAttribute("aria-busy", "true");

    apiFetch("/todo?title=" + encodeURIComponent(title)
        + "&deadline=" + encodeURIComponent(deadline)
        + "&additionalInfo=" + encodeURIComponent(additionalInfo), {
        method: "POST"
    })
        .then(function (response) {
            if (!response.ok) throw new Error("할 일 저장에 실패했어요.");
            return response.json();
        })
        .then(function (todo) {
            return apiFetch("/todo/" + todo.id + "/plan", {
                method: "POST"
            });
        })
        .then(function (response) {
            if (!response.ok) throw new Error("계획 생성에 실패했어요.");
            return response.json();
        })
        .then(function (plan) {
            displayPlan(plan);
        })
        .catch(function (error) {
            alert("요청을 완료하지 못했어요. " + error.message);
        })
        .finally(function () {
            todoButton.disabled = false;
            todoButton.textContent = originalText;
            todoButton.removeAttribute("aria-busy");
        });
});


function displayPlan(plan) {
    const planResult = document.getElementById("planResult");

    // 기존 내용 제거
    planResult.innerHTML = "";

    // 계획 생성 이유
    const reasonTitle = document.createElement("h3");
    reasonTitle.textContent = "계획 이유";

    const reason = document.createElement("p");
    reason.textContent = plan.reason;

    planResult.appendChild(reasonTitle);
    planResult.appendChild(reason);

    // 세부 계획
    plan.items.forEach(function (item) {

        const itemCard = document.createElement("div");
        itemCard.className = "plan-item";

        const content = document.createElement("h4");
        content.textContent = item.content;

        const time = document.createElement("p");
        time.textContent =
            formatDateTime(item.startTime)
            + " ~ "
            + formatDateTime(item.endTime);

        const duration = document.createElement("p");
        duration.textContent =
            "예상 소요시간: " + item.durationMinutes + "분";

        itemCard.appendChild(content);
        itemCard.appendChild(time);
        itemCard.appendChild(duration);

        planResult.appendChild(itemCard);
    });
}


function formatDateTime(dateTime) {
    const date = new Date(dateTime);

    return date.toLocaleString("ko-KR", {
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}
