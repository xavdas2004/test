package projects.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.LinkedList;

import projects.exception.DbException;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	public static final String CATEGORY_TABLE = "category";
	public static final String MATERIAL_TABLE = "material";
	public static final String PROJECT_TABLE = "project";
	public static final String PROJECT_CATEGORY_TABLE = "project_category";
	public static final String STEP_TABLE = "step";

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_id ASC";
		List<Project> projects = new ArrayList<>();
		try (Connection conn = DbConnection.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Project project = new Project();

						project.setActualHours(rs.getBigDecimal("actual_hours"));
						project.setDifficulty(rs.getObject("difficulty", Integer.class));
						project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
						project.setNotes(rs.getString("notes"));
						project.setProjectId(rs.getObject("project_id", Integer.class));
						project.setProjectName(rs.getString("project_name"));

						projects.add(project);
					}
				}
			} catch (Exception e) {
				conn.rollback();
				throw new DbException("Error executing SQL statement", e);
			}
			conn.commit();
		} catch (SQLException e) {
			throw new DbException(e);
		}
		return projects;

	};

	public Project insertProject(Project project) {
		String sql = "INSERT INTO " + PROJECT_TABLE
				+ " (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				stmt.executeUpdate();
			}

			Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
			commitTransaction(conn);

			project.setProjectId(projectId);
			return project;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try {
				Project project = null;
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}

				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				commitTransaction(conn);
				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (

		SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		// TODO Auto-generated method stub
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
		// List<Step> steps = new ArrayList<>();
		// return steps;
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "" + "SELECT c.* FROM " + CATEGORY_TABLE + " c " + "JOIN " + PROJECT_CATEGORY_TABLE
				+ " pc USING(category_id) " + "WHERE project_id = ?";
		// TODO Auto-generated method stub
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		// TODO Auto-generated method stub
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		String sql = "UPDATE " + PROJECT_TABLE + " SET " + 
	"project_name = ?, " + 
	"estimated_hours = ?, " + 
	"actual_hours = ?, " + 
	"difficulty = ?, " + 
	"notes = ? " + 
	"WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			System.out.println("SQL Query: " + sql);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            setParameter(stmt, 1, project.getProjectName(), String.class);
	            setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
	            setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
	            setParameter(stmt, 4, project.getDifficulty(), Integer.class);
	            setParameter(stmt, 5, project.getNotes(), String.class);
	            setParameter(stmt, 6, project.getProjectId(), Integer.class);

	            boolean update = stmt.executeUpdate() == 1;
	            commitTransaction(conn);

	            return update;
	        } catch (SQLException e) {
	            rollbackTransaction(conn);
	            throw new DbException(e);
	        }
	    } catch (SQLException e) {
	        throw new DbException(e);
	    }
	}

	public boolean deleteProject(int projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;

				commitTransaction(conn);
				return deleted;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);

		}
	}
}