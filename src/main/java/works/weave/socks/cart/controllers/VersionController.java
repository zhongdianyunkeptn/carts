package works.weave.socks.cart.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class VersionController {

   @Value("${version}")
   private String version;
   
   private String template = "<table style=\"font-family:sans-serif;font-size:24\"><tr><td>Container image</td><td>%s</td></tr><tr><td>Kubernetes namespace</td><td>%s</td></tr><tr><td>Version</td><td>%s</td></tr></table>";

   @ResponseStatus(HttpStatus.OK)
   @RequestMapping(method = RequestMethod.GET, path = "/")
   public @ResponseBody String getInformation() {
      String namespace = System.getenv("KUBERNETES_NAMESPACE");
      if (namespace == null) {
         namespace = "No namespace found in environment variable KUBERNETES_NAMESPACE";
      }

      String image = System.getenv("CONTAINER_IMAGE");
      if (image == null) {
         image = "No image found in environment variable CONTAINER_IMAGE";
      }

      if (version == null) {
         version = "No version found in application.properties";
      }

      return String.format(template, image, namespace, version);
   }

   @ResponseStatus(HttpStatus.OK)
   @RequestMapping(method = RequestMethod.GET, path = "/version")
   public @ResponseBody String getVersion() {
      return "Version = " + version;
   }

}
