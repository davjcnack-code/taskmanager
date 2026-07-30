import { useEffect, useState } from "react";
import AuthForm from "./components/AuthForm";
import TaskStats from "./components/TaskStats";
import TaskForm from "./components/TaskForm";
import TaskList from "./components/TaskList";
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

                    <TaskStats
                        totalTasks={totalTasks}
                        activeTasks={activeTasks}
                        completedTasks={completedTasks}
                    />

                    <TaskForm
                        taskTitle={taskTitle}
                        taskDescription={taskDescription}
                        setTaskTitle={setTaskTitle}
                        setTaskDescription={setTaskDescription}
                        handleCreateTask={handleCreateTask}
                    />

                    <TaskList
                        tasks={tasks}
                        filteredTasks={filteredTasks}
                        filter={filter}
                        setFilter={setFilter}
                        totalTasks={totalTasks}
                        activeTasks={activeTasks}
                        completedTasks={completedTasks}
                        editingTaskId={editingTaskId}
                        editTitle={editTitle}
                        editDescription={editDescription}
                        setEditTitle={setEditTitle}
                        setEditDescription={setEditDescription}
                        handleSaveEdit={handleSaveEdit}
                        handleCancelEdit={handleCancelEdit}
                        handleToggleCompleted={handleToggleCompleted}
                        handleStartEdit={handleStartEdit}
                        handleDeleteTask={handleDeleteTask}
                    />
                </div>
            </div>
        );
    }

    return (
        <AuthForm
            mode={mode}
            setMode={setMode}
            name={name}
            setName={setName}
            email={email}
            setEmail={setEmail}
            password={password}
            setPassword={setPassword}
            handleRegister={handleRegister}
            handleLogin={handleLogin}
            message={message}
        />
    );
}

export default App;
