const diaryTab = document.getElementById("diaryTab");
const planTab = document.getElementById("planTab");
const recordsResult = document.getElementById("recordsResult");


// 처음 들어오면 일기 기록 표시
loadDiaries();


// ==================== 탭 ====================

diaryTab.addEventListener("click", function () {
    diaryTab.classList.add("active");
    planTab.classList.remove("active");

    loadDiaries();
});


planTab.addEventListener("click", function () {
    planTab.classList.add("active");
    diaryTab.classList.remove("active");

    loadPlans();
});


// ==================== 일기 기록 ====================

function loadDiaries() {

    fetch("/diary")
        .then(function (response) {
            return response.json();
        })
        .then(function (diaries) {
            displayDiaries(diaries);
        });
}


function displayDiaries(diaries) {

    recordsResult.innerHTML = "";

    if (diaries.length === 0) {
        recordsResult.textContent = "아직 작성한 일기가 없습니다.";
        return;
    }

    diaries.forEach(function (diary) {

        const diaryCard = document.createElement("div");
        diaryCard.className = "record-card";

        // 목록에서는 날짜만 표시
        const date = document.createElement("h3");
        date.textContent = formatDiaryDate(diary.createdAt);
        date.style.cursor = "pointer";

        const deleteButton = document.createElement("button");
        deleteButton.className = "record-delete-button";
        deleteButton.textContent = "삭제";

        // 일기 내용은 처음에는 숨김
        const content = document.createElement("p");
        content.textContent = diary.content;
        content.style.display = "none";

        // 날짜를 클릭하면 일기 내용 펼치기 / 접기
        date.addEventListener("click", function () {

            if (content.style.display === "none") {
                content.style.display = "block";
            } else {
                content.style.display = "none";
            }
        });

        deleteButton.addEventListener("click", function () {
            deleteDiary(diary.id, diaryCard);
        });

        diaryCard.appendChild(date);
        diaryCard.appendChild(deleteButton);
        diaryCard.appendChild(content);

        recordsResult.appendChild(diaryCard);
    });
}


function deleteDiary(diaryId, diaryCard) {

    if (!confirm("이 일기를 삭제할까요?")) {
        return;
    }

    fetch("/diary/" + diaryId, {
        method: "DELETE"
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error("일기를 삭제하지 못했습니다.");
            }

            diaryCard.remove();

            if (recordsResult.children.length === 0) {
                recordsResult.textContent = "아직 작성한 일기가 없습니다.";
            }
        })
        .catch(function (error) {
            alert(error.message);
        });
}


// ==================== 계획 기록 ====================

function loadPlans() {

    fetch("/plans")
        .then(function (response) {
            return response.json();
        })
        .then(function (plans) {
            displayPlans(plans);
        });
}


function displayPlans(plans) {

    recordsResult.innerHTML = "";

    if (plans.length === 0) {
        recordsResult.textContent = "아직 생성된 계획이 없습니다.";
        return;
    }

    plans.forEach(function (plan) {

        const planCard = document.createElement("div");
        planCard.className = "record-card";
        planCard.style.cursor = "pointer";

        const title = document.createElement("h3");
        title.textContent = plan.todo.title;

        const deadline = document.createElement("p");
        deadline.textContent =
            "마감일: " + formatDateTime(plan.todo.deadline);

        const reason = document.createElement("p");
        reason.textContent =
            "계획 이유: " + plan.reason;

        const detail = document.createElement("div");
        detail.className = "plan-record-detail";
        detail.style.display = "none";

        const deleteButton = document.createElement("button");
        deleteButton.className = "record-delete-button";
        deleteButton.textContent = "삭제";

        planCard.addEventListener("click", function () {
            togglePlanDetail(plan.todo.id, detail);
        });

        deleteButton.addEventListener("click", function (event) {
            event.stopPropagation();
            deletePlan(plan.id, planCard);
        });

        planCard.appendChild(title);
        planCard.appendChild(deleteButton);
        planCard.appendChild(deadline);
        planCard.appendChild(reason);
        planCard.appendChild(detail);

        recordsResult.appendChild(planCard);
    });
}


function togglePlanDetail(todoId, detail) {

    if (detail.style.display === "block") {
        detail.style.display = "none";
        return;
    }

    if (detail.dataset.loaded === "true") {
        detail.style.display = "block";
        return;
    }

    detail.textContent = "계획을 불러오는 중입니다.";
    detail.style.display = "block";

    fetch("/todo/" + todoId + "/plan")
        .then(function (response) {
            if (!response.ok) {
                throw new Error("계획을 불러오지 못했습니다.");
            }

            return response.json();
        })
        .then(function (plan) {
            displayPlanDetail(plan, detail);
        })
        .catch(function (error) {
            detail.textContent = error.message;
        });
}


function displayPlanDetail(plan, detail) {

    detail.innerHTML = "";
    detail.dataset.loaded = "true";

    const itemList = document.createElement("div");
    itemList.className = "plan-record-items";

    plan.items.forEach(function (item) {
        const itemCard = document.createElement("div");
        itemCard.className = "plan-record-item";

        const content = document.createElement("strong");
        content.textContent = item.content;

        const time = document.createElement("p");
        time.textContent =
            formatDateTime(item.startTime)
            + " ~ "
            + formatDateTime(item.endTime)
            + " / "
            + item.durationMinutes
            + "분";

        itemCard.appendChild(content);
        itemCard.appendChild(time);
        itemList.appendChild(itemCard);
    });

    detail.appendChild(itemList);
}


function deletePlan(planId, planCard) {

    if (!confirm("이 계획을 삭제할까요?")) {
        return;
    }

    fetch("/plans/" + planId, {
        method: "DELETE"
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error("계획을 삭제하지 못했습니다.");
            }

            planCard.remove();

            if (recordsResult.children.length === 0) {
                recordsResult.textContent = "아직 생성된 계획이 없습니다.";
            }
        })
        .catch(function (error) {
            alert(error.message);
        });
}


// ==================== 날짜 표시 ====================

// 일기 날짜
function formatDiaryDate(dateTime) {

    if (!dateTime) {
        return "날짜 정보 없음";
    }

    const date = new Date(dateTime);

    return date.toLocaleDateString("ko-KR", {
        year: "numeric",
        month: "long",
        day: "numeric",
        weekday: "short"
    });
}


// Todo 마감 날짜
function formatDateTime(dateTime) {

    const date = new Date(dateTime);

    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}
