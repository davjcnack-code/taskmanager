import { useEffect, useState } from "react";
import AuthForm from "./components/AuthForm";
import TaskStats from "./components/TaskStats";
import TaskForm from "./components/TaskForm";
import TaskList from "./components/TaskList";
import "./App.css";
import {
    registerUser,
    loginUser,
    getTasks,
    createTask,
    updateTask,
    deleteTask,
} from "./api";

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
            await registerUser({
                name,
                email,
                password,
            });

            setMessage("Registration successful. You can log in now.");
            setMode("login");
        } catch (error) {
            setMessage(getErrorMessage(error, "Registration failed"));
        }
    }

    async function handleLogin(event) {
        event.preventDefault();
        setMessage("");

        try {
            const data = await loginUser({
                email,
                password,
            });

            localStorage.setItem("token", data.token);
            setToken(data.token);
            setMessage(`Welcome, ${data.name}`);
        } catch (error) {
            setMessage(getErrorMessage(error, "Login failed"));
        }
    }

    async function fetchTasks() {
        try {
            const data = await getTasks(token);
            setTasks(data);
        } catch (error) {
            handleApiError(error, "Could not load tasks.");
        }
    }

    async function handleCreateTask(event) {
        event.preventDefault();
        setMessage("");

        try {
            const newTask = await createTask(token, {
                title: taskTitle,
                description: taskDescription,
                completed: false,
            });

            setTaskTitle("");
            setTaskDescription("");
            setTasks([...tasks, newTask]);
            setMessage("Task created.");
        } catch (error) {
            handleApiError(error, "Could not create task");
        }
    }

    async function handleToggleCompleted(task) {
        setMessage("");

        try {
            const updatedTask = await updateTask(token, task.id, {
                title: task.title,
                description: task.description,
                completed: !task.completed,
            });

            setTasks(
                tasks.map((currentTask) =>
                    currentTask.id === task.id ? updatedTask : currentTask
                )
            );

            setMessage("Task updated.");
        } catch (error) {
            handleApiError(error, "Could not update task");
        }
    }

    async function handleDeleteTask(taskId) {
        setMessage("");

        try {
            await deleteTask(token, taskId);

            setTasks(tasks.filter((task) => task.id !== taskId));
            setMessage("Task deleted.");
        } catch (error) {
            handleApiError(error, "Could not delete task");
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

    function getErrorMessage(error, fallbackMessage) {
        if (!error.status) {
            return "Could not connect to backend.";
        }

        return (
            error.data?.title ||
            error.data?.description ||
            error.data?.error ||
            fallbackMessage
        );
    }

    function handleApiError(error, fallbackMessage) {
        if (error.status === 401 || error.status === 403) {
            handleAuthExpired();
            return;
        }

        setMessage(getErrorMessage(error, fallbackMessage));
    }

    async function handleSaveEdit(event, task) {
        event.preventDefault();
        setMessage("");

        try {
            const updatedTask = await updateTask(token, task.id, {
                title: editTitle,
                description: editDescription,
                completed: task.completed,
            });

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
            handleApiError(error, "Could not update task");
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
