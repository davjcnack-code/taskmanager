package com.example.taskmanager.task;
import org.springframework.data.jpa.repository.JpaRepository;

/*This creates a repository. A repository is the class Spring uses to work with the database.
* We extend a built-in databse methods.
* Task mean this repository works with the Task entity and
* long mean the id type is long. Method given to us, findall(), findById(), save(), deleteById() etc... */
public interface TaskRepository extends JpaRepository<Task, Long>{

}
