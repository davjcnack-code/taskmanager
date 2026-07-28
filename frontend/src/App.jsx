import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8080";

function App() {
    const [mode, setMode] = useState("login");

    const [name, setName] = useState("");
    const [email, setEmail] = useState("david@example.com");
    const [password, setPassword] = useState("password123");

    const [message, setMessage] = useState("");
    const [token, setToken] = useState(localStorage.getItem("token"));

    const [tasks, setTasks] = useState([]);
    const [taskTitle, setTaskTitle] = useState("");
    const [taskDescription, setTaskDescription] = useState("");

    const [editingTaskId, setEditingTaskId] = useState(null);
    const [editTitle, setEditTitle] = useState("");
    const [editDescription, setEditDescription] = useState("");

    const [filter, setFilter] = useState("all");

    const filteredTasks = tasks.filter((task) => {
        if (filter === "active") {
            return !task.completed;
        }

        if (filter === "completed") {
            return task.completed;
        }

        return true;
    });

    const totalTasks = tasks.length;
    const completedTasks = tasks.filter((task) => task.completed).length;
    const activeTasks = tasks.filter((task) => !task.completed).length;

    useEffect(() => {
        if (token) {
            fetchTasks();
        }
    }, [token]);

    async function handleRegister(event) {
        event.preventDefault();
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/users/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name,
                    email,
                    password,
                }),
            });

            const data = await response.json();

            if (!response.ok) {
                setMessage(data.error || "Registration failed");
                return;
            }

            setMessage("Registration successful. You can log in now.");
            setMode("login");
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    async function handleLogin(event) {
        event.preventDefault();
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/users/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email,
                    password,
                }),
            });

            const data = await response.json();

            if (!response.ok) {
                setMessage(data.error || "Login failed");
                return;
            }

            localStorage.setItem("token", data.token);
            setToken(data.token);
            setMessage(`Welcome, ${data.name}`);
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    async function fetchTasks() {
        try {
            const response = await fetch(`${API_URL}/tasks`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 401 || response.status === 403) {
                handleAuthExpired();
                return;
            }

            if (!response.ok) {
                setMessage("Could not load tasks.");
                return;
            }

            const data = await response.json();
            setTasks(data);
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    async function handleCreateTask(event) {
        event.preventDefault();
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/tasks`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    title: taskTitle,
                    description: taskDescription,
                    completed: false,
                }),
            });

            if (response.status === 401 || response.status === 403) {
                handleAuthExpired();
                return;
            }

            const data = await response.json();

            if (!response.ok) {
                setMessage(data.title || data.description || data.error || "Could not create task");
                return;
            }

            setTaskTitle("");
            setTaskDescription("");
            setTasks([...tasks, data]);
            setMessage("Task created.");
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    async function handleToggleCompleted(task) {
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/tasks/${task.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    title: task.title,
                    description: task.description,
                    completed: !task.completed,
                }),
            });

            if (response.status === 401 || response.status === 403) {
                handleAuthExpired();
                return;
            }

            const updatedTask = await response.json();

            if (!response.ok) {
                setMessage(updatedTask.error || "Could not update task");
                return;
            }

            setTasks(
                tasks.map((currentTask) =>
                    currentTask.id === task.id ? updatedTask : currentTask
                )
            );

            setMessage("Task updated.");
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    async function handleDeleteTask(taskId) {
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/tasks/${taskId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 401 || response.status === 403) {
                handleAuthExpired();
                return;
            }

            if (!response.ok) {
                setMessage("Could not delete task");
                return;
            }

            setTasks(tasks.filter((task) => task.id !== taskId));
            setMessage("Task deleted.");
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    function handleStartEdit(task) {
        setEditingTaskId(task.id);
        setEditTitle(task.title);
        setEditDescription(task.description);
        setMessage("");
    }

    function handleAuthExpired() {
        localStorage.removeItem("token");
        setToken(null);
        setTasks([]);
        setMessage("Your session expired. Please log in again.");
    }

    function handleCancelEdit() {
        setEditingTaskId(null);
        setEditTitle("");
        setEditDescription("");
    }

    async function handleSaveEdit(event, task) {
        event.preventDefault();
        setMessage("");

        try {
            const response = await fetch(`${API_URL}/tasks/${task.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    title: editTitle,
                    description: editDescription,
                    completed: task.completed,
                }),
            });

            if (response.status === 401 || response.status === 403) {
                handleAuthExpired();
                return;
            }

            const updatedTask = await response.json();

            if (!response.ok) {
                setMessage(updatedTask.title || updatedTask.description || updatedTask.error || "Could not update task");
                return;
            }

            setTasks(
                tasks.map((currentTask) =>
                    currentTask.id === task.id ? updatedTask : currentTask
                )
            );

            setEditingTaskId(null);
            setEditTitle("");
            setEditDescription("");
            setMessage("Task saved.");
        } catch (error) {
            setMessage("Could not connect to backend.");
        }
    }

    function handleLogout() {
        localStorage.removeItem("token");
        setToken(null);
        setTasks([]);
        setMessage("Logged out.");
    }

    if (token) {
        return (
            <div className="app">
                <div className="card wide">
                    <div className="header">
                        <h1>Task Manager</h1>
                        <button onClick={handleLogout}>Logout</button>
                    </div>

                    {message && <div className="message">{message}</div>}

                    <div className="stats">
                        <div className="stat-card">
                            <strong>{totalTasks}</strong>
                            <span>Total</span>
                        </div>

                        <div className="stat-card">
                            <strong>{activeTasks}</strong>
                            <span>Active</span>
                        </div>

                        <div className="stat-card">
                            <strong>{completedTasks}</strong>
                            <span>Completed</span>
                        </div>
                    </div>

                    <form className="task-form" onSubmit={handleCreateTask}>
                        <h2>Create Task</h2>

                        <label>Title</label>
                        <input
                            type="text"
                            value={taskTitle}
                            onChange={(event) => setTaskTitle(event.target.value)}
                            placeholder="Enter task title"
                        />

                        <label>Description</label>
                        <input
                            type="text"
                            value={taskDescription}
                            onChange={(event) => setTaskDescription(event.target.value)}
                            placeholder="Enter task description"
                        />

                        <button type="submit">Add Task</button>
                    </form>

                    <div className="task-list">
                        <h2>Your Tasks</h2>
                        <div className="filter-buttons">
                            <button
                                className={filter === "all" ? "active" : "secondary"}
                                onClick={() => setFilter("all")}
                            >
                                All ({totalTasks})
                            </button>

                            <button
                                className={filter === "active" ? "active" : "secondary"}
                                onClick={() => setFilter("active")}
                            >
                                Active ({activeTasks})
                            </button>

                            <button
                                className={filter === "completed" ? "active" : "secondary"}
                                onClick={() => setFilter("completed")}
                            >
                                Completed ({completedTasks})
                            </button>
                        </div>

                        {tasks.length === 0 ? (
                            <p>No tasks yet.</p>
                        ) : filteredTasks.length === 0 ? (
                            <p>No tasks match this filter.</p>
                        ) : (
                            filteredTasks.map((task) => (
                                <div className="task-item" key={task.id}>
                                    {editingTaskId === task.id ? (
                                        <form className="edit-form" onSubmit={(event) => handleSaveEdit(event, task)}>
                                            <label>Title</label>
                                            <input
                                                type="text"
                                                value={editTitle}
                                                onChange={(event) => setEditTitle(event.target.value)}
                                            />

                                            <label>Description</label>
                                            <input
                                                type="text"
                                                value={editDescription}
                                                onChange={(event) => setEditDescription(event.target.value)}
                                            />

                                            <div className="task-actions">
                                                <button type="submit">Save</button>

                                                <button type="button" className="secondary" onClick={handleCancelEdit}>
                                                    Cancel
                                                </button>
                                            </div>
                                        </form>
                                    ) : (
                                        <>
                                            <h3 className={task.completed ? "completed-title" : ""}>
                                                {task.title}
                                            </h3>

                                            <p>{task.description}</p>

                                            <span className={task.completed ? "completed-badge" : ""}>
                                                 {task.completed ? "Completed" : "Not completed"}
                                            </span>

                                            <div className="task-actions">
                                                <button onClick={() => handleToggleCompleted(task)}>
                                                    {task.completed ? "Mark Not Completed" : "Mark Completed"}
                                                </button>

                                                <button className="secondary" onClick={() => handleStartEdit(task)}>
                                                    Edit
                                                </button>

                                                <button className="danger" onClick={() => handleDeleteTask(task.id)}>
                                                    Delete
                                                </button>
                                            </div>
                                        </>
                                    )}
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="app">
            <div className="card">
                <h1>Task Manager</h1>

                <div className="tabs">
                    <button
                        className={mode === "login" ? "active" : "secondary"}
                        onClick={() => setMode("login")}
                    >
                        Login
                    </button>

                    <button
                        className={mode === "register" ? "active" : "secondary"}
                        onClick={() => setMode("register")}
                    >
                        Register
                    </button>
                </div>

                {mode === "register" ? (
                    <form onSubmit={handleRegister}>
                        <label>Name</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            placeholder="Enter your name"
                        />

                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="Enter your email"
                        />

                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="Enter your password"
                        />

                        <button type="submit">Create Account</button>
                    </form>
                ) : (
                    <form onSubmit={handleLogin}>
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="Enter your email"
                        />

                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="Enter your password"
                        />

                        <button type="submit">Login</button>
                    </form>
                )}

                {message && <div className="message">{message}</div>}
            </div>
        </div>
    );
}

export default App;
