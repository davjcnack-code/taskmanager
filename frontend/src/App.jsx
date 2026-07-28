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

            if (!response.ok) {
                setMessage("Could not load tasks. Please log in again.");
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

                        {tasks.length === 0 ? (
                            <p>No tasks yet.</p>
                        ) : (
                            tasks.map((task) => (
                                <div className="task-item" key={task.id}>
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

                                        <button className="danger" onClick={() => handleDeleteTask(task.id)}>
                                            Delete
                                        </button>
                                    </div>
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
