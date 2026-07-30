import TaskItem from "./TaskItem";

function TaskList({
    tasks,
    filteredTasks,
    filter,
    setFilter,
    totalTasks,
    activeTasks,
    completedTasks,
    editingTaskId,
    editTitle,
    editDescription,
    setEditTitle,
    setEditDescription,
    handleSaveEdit,
    handleCancelEdit,
    handleToggleCompleted,
    handleStartEdit,
    handleDeleteTask,
   }) {
    return (
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
                    <TaskItem
                    key={task.id}
                    task={task}
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
                ))
            )}
        </div>
    );
}

export default TaskList;