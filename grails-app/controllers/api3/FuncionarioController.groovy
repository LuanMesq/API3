package api3

import grails.converters.JSON
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import grails.gorm.transactions.Transactional

@Transactional
class FuncionarioController {
    static responseFormats = ["json"]
    static defaultAction = "get"
    def LogService
    static allowedMethods = [
            save: "POST",
            list: "GET",
            update: "PUT",
            delete: "DELETE",
            get: "GET"
    ]

    def list(){
        def api2Url = "http://localhost:8080/api2/funcionario/list"
        def response = sendRequestToApi2Get(api2Url)
        def json = new JSON().parse(response)
        respond(json)
    }

    def get(Long id){
        def api2Url = "http://localhost:8080/api2/funcionario/get/${id}"
        def response = sendRequestToApi2Get(api2Url)
        def json = new JSON().parse(response)
        respond(json)
    }

    def save(){
        def requestData = request.JSON
        def api2Url = "http://localhost:8080/api2/funcionario/save"
        def responseEntity = sendRequestToApi2(api2Url, requestData)
        def responseBody = responseEntity.body

        if (responseBody) {
            def json = JSON.parse(responseBody)
            def logData = [descricao: "Resposta da API2: ${json}"]
            LogService.salvarLog(logData as Map)
            respond(json)
        } else {
            respond([error: "Resposta vazia da API2"])
        }
    }

    def update(Long id){
        def requestData = request.JSON
        def api2Url = "http://localhost:8080/api2/funcionario/update/${id}"
        def responseEntity = sendRequestToApi2Put(api2Url, requestData)
        def responseBody = responseEntity.body

        if (responseBody) {
            def json = JSON.parse(responseBody)
            def logData = [descricao: "Resposta da API2: ${json}"]
            LogService.salvarLog(logData as Map)
            respond(json)
        } else {
            respond([error: "Resposta vazia da API2"])
        }
    }

    def delete(Long id){
        def api2Url = "http://localhost:8080/api2/funcionario/delete/${id}"
        def response = sendRequestToApi2Delete(api2Url)
        Map logData = [descricao: "Resposta da API2: ${response}"]
        log.error("logData: ${logData}")
        def retorno = LogService.salvarLog(logData as Map)
        log.error("retorno: ${retorno}")
        def json = new JSON().parse(response)
        respond(json)
    }

    private ResponseEntity<String> sendRequestToApi2(String apiUrl, def requestData) {
        RestTemplate restTemplate = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> entity = new HttpEntity<>(requestData.toString(), headers)
        return restTemplate.postForEntity(apiUrl, entity, String.class)
    }

    private ResponseEntity<String> sendRequestToApi2Put(String apiUrl, def requestData) {
        RestTemplate restTemplate = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> entity = new HttpEntity<>(requestData, headers)

        return restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class)
    }

    private sendRequestToApi2Delete(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> entity = new HttpEntity<>(headers)
        return restTemplate.exchange(apiUrl, HttpMethod.DELETE, entity, String.class).body
    }

    private sendRequestToApi2Get(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> entity = new HttpEntity<>(headers)
        return restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class).body
    }

}
