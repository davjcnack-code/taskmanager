function TaskForm({
    taskTitle,
    taskDescription,
    setTaskTitle,
    setTaskDescription,
    handleCreateTask,
   }) {
    return (
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
    );
}

export default TaskForm;