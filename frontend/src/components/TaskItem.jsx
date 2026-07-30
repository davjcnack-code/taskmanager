function TaskItem({
    task,
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
        <div className="task-item">
            {editingTaskId === task.id ? (
                <form className="edit-form" onSubmit={(event) => handleSaveEdit(event,task)}>
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

                    <div className="task-action">
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
    );
}

export default TaskItem;