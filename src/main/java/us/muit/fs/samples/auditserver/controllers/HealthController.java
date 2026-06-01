package us.muit.fs.samples.auditserver.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import us.muit.fs.samples.auditserver.config.AppProperties;

@RestController
public class HealthController {

@Autowired
private AppProperties config;

private static Logger log = Logger.getLogger(HealthController.class.getName());

private String getHealthzGithubRepo() {
return config.getHealthzGithubRepo();
}

@GetMapping(path = "/readyz", produces = MediaType.APPLICATION_JSON_VALUE)
ResponseEntity<Map<String, Object>> healthz() {
Map<String, Object> body = new HashMap<>();
String healthzGithubRepo = this.getHealthzGithubRepo();

body.put("remoteRepo", healthzGithubRepo);

try {
GitHub github = GitHubBuilder.fromEnvironment().build();
GHRepository repository = github.getRepository(healthzGithubRepo);

body.put("healthy", true);
body.put("repository", repository.getFullName());
body.put("defaultBranch", repository.getDefaultBranch());

log.fine("Repositorio remoto accesible: " + repository.getFullName());

return ResponseEntity.status(HttpStatus.OK).body(body);

} catch (Exception ref) {
log.warning("Se ha recibido esta excepcion: " + ref);

body.put("healthy", false);
body.put("error", ref.toString());

return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
}
}
}
