package com.lreas.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class DatabaseApplicationTests {
	@Autowired
	private SettingRepository settingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceAccessedByRepository resourceAccessedByRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

	@Autowired
	private QuizVersionRepository quizVersionRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void contextLoads() {
//		QuizVersion quizVersion = new QuizVersion();
//		quizVersion.setTitle("test");
//		quizVersion.setDescription("test");
//		quizVersionRepository.save(quizVersion);
//
//		Question question = new Question();
//		question.setTitle("yo");
//		question.setQuizVersion(quizVersion);
//		questionRepository.save(question);
//
//		quizVersion.setQuestions(List.of(question));
//		quizVersionRepository.save(quizVersion);
//
//		Optional<QuizVersion> quizVersion2 = quizVersionRepository.findById(
//				quizVersion.getId()
//		);
//		if (quizVersion2.isPresent()) {
//			System.out.print("quizVersion: ");
//			System.out.println(quizVersion2.get().getQuestions().size());
//		}
//		else {
//			System.out.print("Noooooo");
//		}

		// load gemini settings
//		this.loadGeminiSettings();

		// load minio settings
//		this.loadMinioSettings();

		// load user info
//		User user = this.loadUserInfo();

		// load resource info
//		this.loadResourcesForAdmin(user);
	}

	private void loadGeminiSettings() {
		Setting setting = settingRepository.findByName("GEMINI_API_KEY");
		if (setting == null) {
			setting = new Setting();
			setting.setName("GEMINI_API_KEY");
			setting.setValue("AIzaSyBp4h7en6qZoEnFjYq-xq1GzK57KUboUmA");
			settingRepository.save(setting);
		}
	}

	private void loadMinioSettings() {
		Setting setting1 = settingRepository.findByName("MINIO_ACCESS_KEY");
		if (setting1 == null) {
			setting1 = new Setting();
			setting1.setName("MINIO_ACCESS_KEY");
			setting1.setValue("X14mbYwCCyH77aadb49p");
			settingRepository.save(setting1);
		}

		Setting setting2 = settingRepository.findByName("MINIO_SECRET_KEY");
		if (setting2 == null) {
			setting2 = new Setting();
			setting2.setName("MINIO_SECRET_KEY");
			setting2.setValue("SlAQJjb2n3IpzKtjguFZdoF05iEjmuYJZonbGYTt");
			settingRepository.save(setting2);
		}
	}

	private User loadUserInfo() {
		User user = userRepository.findByUsername("admin");
		if (user == null) {
			user = new User();
			user.setUsername("admin");
			user.setPassword("admin");
			user.setWorkflowState(User.STATE.ACTIVE);
			user.setEmail("admin@admin.com");
			user.setAvtPath("");
			user.setRole(User.ROLE.ADMIN);

			Institution institution = new Institution();
			institution.setName("admin");
			institutionRepository.save(institution);

			user.setInstitution(institution);
			userRepository.save(user);
		}
		return user;
	}

	private void loadResourcesForAdmin(User user) {
		String name = "Your Folders";
		Resource resource = resourceRepository.findOneByName(name);
		if (resource == null) {
			Date date = new Date();
			resource = new Resource();
			UUID uuid = UUID.randomUUID();

			resource.setDateUpdated(date);
			resource.setWorkflowState(Resource.STATE.AVAILABLE);
			resource.setDateCreated(date);
			resource.setName(name);
			resource.setIsFolder(true);
			resource.setParent(null);
			resource.setUser(user);
			resourceRepository.save(resource);
		}

		ResourceAccessedBy resourceAccessedBy = resourceAccessedByRepository.findByResourceResourceAndResourceUser(resource, user);
		if (resourceAccessedBy == null) {
			ResourceAccessedById resourceAccessedById = new ResourceAccessedById();
			resourceAccessedById.setUser(user);
			resourceAccessedById.setResource(resource);

			resourceAccessedBy = new ResourceAccessedBy();
			resourceAccessedBy.setResource(resourceAccessedById);
			resourceAccessedBy.setRole(ResourceAccessedBy.ROLE.OWNER);
			resourceAccessedByRepository.save(resourceAccessedBy);
		}
	}
}
