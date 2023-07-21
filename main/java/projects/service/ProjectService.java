package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
//import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);	
	}

	public List<Project> fetchAllProjects() {
		// TODO Auto-generated method stub
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				()-> new NoSuchElementException(
				"Project with Project ID=" + projectId + " does not exist."));
	}
		//if (op.isPresent()) {
			//return op.get();
		//} else {
		// throw new NoSuchElementException("Project not found");
	//	}

	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + "does not exist.");
		}
	}
	}


