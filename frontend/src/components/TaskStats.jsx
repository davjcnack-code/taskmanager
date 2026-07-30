function TaskStats({totalTasks, activeTasks, completedTasks}){
    return(
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
    );
}
export default TaskStats;