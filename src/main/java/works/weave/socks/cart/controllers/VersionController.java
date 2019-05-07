package works.weave.socks.cart.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class VersionController {

   @Value("${version}")
   private String version;
   @Value("${delayInMillis}")
   private String delayInMillis;
   @Value("${promotionRate}")
   private String promotionRate;

   private String template = "<html><head><style>html,body{width:100%%;padding:0;margin:0}.centered-wrapper{position:relative;text-align:center;margin-top:20px}.centered-content{display:inline-block;vertical-align:top}</style></head><body class=\"centered-wrapper\"><div class=\"centered-content\">"+
      "<table style=\"font-family:sans-serif;font-size:16\" cellspacing=\"5\"><tbody>" +
      "<tr><td rowspan=\"7\" width=\"150\"><img src=\"https://raw.githubusercontent.com/keptn-sockshop/carts/master/cart.png\" width=\"150\"></td>"+
      "<td align=\"right\" style=\"color:silver\">Pod name:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Container image:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Deployment name:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Kubernetes namespace:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Version:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Delay in ms:</td><td>%s</td></tr>"+
      "<tr><td align=\"right\" style=\"color:silver\">Promotion rate:</td><td>%s</td></tr>"+
      "</tbody></table></div></body></html>";

   @ResponseStatus(HttpStatus.OK)
   @RequestMapping(method = RequestMethod.GET, path = "/")
   public @ResponseBody String getInformation() {
      String name = getEnvVarValueOrNotFoundMessage("POD_NAME");
      String namespace = getEnvVarValueOrNotFoundMessage("KUBERNETES_NAMESPACE");
      String deployment = getEnvVarValueOrNotFoundMessage("DEPLOYMENT_NAME");
      String image = getEnvVarValueOrNotFoundMessage("CONTAINER_IMAGE");

      if (version == null) {
         version = "No version found in application.properties";
      }
      if (delayInMillis == null) {
         delayInMillis = "No delayInMillis found in application.properties";
      }
      if (promotionRate == null) {
         promotionRate = "No promotionRate found in application.properties";
      }

      return String.format(template, name, image, deployment, namespace, version, delayInMillis, promotionRate);
   }

   private String getEnvVarValueOrNotFoundMessage(String var) {
      String value = System.getenv(var);
      if (value == null) {
         value = "No value found in environment variable " + var;
      }
      return value;
   }

   @ResponseStatus(HttpStatus.OK)
   @RequestMapping(method = RequestMethod.GET, path = "/version")
   public @ResponseBody String getVersion() {
      return "Version = " + version;
   }

}
