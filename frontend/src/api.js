const API_URL = "http://localhost:8080";

async function request(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, options);

    let data = null;

    try {
        data = await response.json();
    } catch (error) {
        data = null;
    }

    if (!response.ok) {
        const error = new Error("API request failed");
        error.status = response.status;
        error.data = data;
        throw error;
    }

    return data;
}

export function registerUser(userData) {
    return request("/users/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    });
}

export function loginUser(loginData) {
    return request("/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(loginData),
    });
}

export function getTasks(token) {
    return request("/tasks", {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
}

export function createTask(token, taskData) {
    return request("/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(taskData),
    });
}

export function updateTask(token, taskId, taskData) {
    return request(`/tasks/${taskId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(taskData),
    });
}

export function deleteTask(token, taskId) {
    return request(`/tasks/${taskId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
}








