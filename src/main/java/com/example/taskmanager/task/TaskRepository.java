package com.example.taskmanager.task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*This creates a repository. A repository is the class Spring uses to work with the database.
* We extend a built-in databse methods.
* Task mean this repository works with the Task entity and
* long mean the id type is long. Method given to us, findall(), findById(), save(), deleteById() etc... */
public interface TaskRepository extends JpaRepository<Task, Long>{
        List<Task> findByUserEmail(String email);

        Optional<Task> findByIdAndUserEmail(Long id, String email);

        boolean existsByIdAndUserEmail(Long id, String email);
}
