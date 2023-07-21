package projects;

import java.math.BigDecimal;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
//import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private ProjectService projectService;
	private List<String> operations = List.of("1) Add a project", "2) List projects", "3) Select a project",
			"4) Update project details");
	private Scanner scanner = new Scanner(System.in);

	Project curProject;

	public ProjectsApp() {
		projectService = new ProjectService();
	}

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
				e.printStackTrace();
			}

		}
	}

	private void updateProjectDetails() {
		// TODO Auto-generated method stub
		if (curProject == null) {
			System.out.println("\nPlease select a project.");
			return;
		}
		System.out.println("\nProject details:");
		String projectName = getStringInput("Enter project name: " + curProject.getProjectName());
		String projectId = getStringInput("Enter project id: " + curProject.getProjectId());
		String estimatedHours = getStringInput("Enter estimated hours: " + curProject.getEstimatedHours());
		String actualHours = getStringInput("Enter actual hours " + curProject.getActualHours());
		String difficulty = getStringInput("Enter difficulty: " + curProject.getDifficulty());
		String notes = getStringInput("Enter notes: " + curProject.getNotes());

		Project project = new Project();

		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setProjectId(Objects.isNull(projectId) ? curProject.getProjectId() : Integer.parseInt(projectId));
		project.setEstimatedHours(
				Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : new BigDecimal(estimatedHours));
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : new BigDecimal(actualHours));
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : Integer.parseInt(difficulty));
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);

		project.setProjectId(curProject.getProjectId());
		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void selectProject() {
		// TODO Auto-generated method stub
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		curProject = null;

		try {
			curProject = projectService.fetchProjectById(projectId);
			System.out.println("Project selected: " + curProject.getProjectName());
		} catch (NoSuchElementException e) {
			System.out.println("Invalid project ID. Please try again.");
		}
	}

	private void listProjects() {
		// TODO Auto-generated method stub
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");

		projects.forEach(
				project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		int difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created a new project. " + dbProject);
	}

	private BigDecimal getDecimalInput(String string) {
		String input = getStringInput(string);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number. " + e);
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting menu");
		return true;
	}

	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.\nTry again.\n");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou do not have an active project.");
		} else {
			System.out.println("\n You are viewing: " + curProject);
		}
	}

	public Project getCurProject() {
		return curProject;
	}

	public void setCurProject(Project curProject) {
		this.curProject = curProject;
	}

}