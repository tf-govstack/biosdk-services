package io.mosip.biosdk.services.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.biosdk.services.config.LoggerConfig;
import io.mosip.biosdk.services.constants.ErrorMessages;
import io.mosip.biosdk.services.dto.*;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.factory.BioSdkServiceFactory;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.biometrics.spi.IBioApi;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;

import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_IDTYPE;
import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_SESSIONID;

@RestController
@RequestMapping("/")
@Tag(name = "Sdk", description = "Sdk")
@CrossOrigin("*")
public class MainController {

    private Logger logger = LoggerConfig.logConfig(MainController.class);

    @Autowired
    private Utils serviceUtil;

    @Autowired
    private IBioApi iBioApi;

    @Autowired
    private BioSdkServiceFactory bioSdkServiceFactory;

    private Gson gson = new GsonBuilder().serializeNulls().create();;

    @GetMapping(path = "/")
    @Operation(summary = "Service status", description = "Service status", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Service is running...", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> status() {
        Date d = new Date();
        return ResponseEntity.status(HttpStatus.OK).body("Service is running... "+d.toString());
    }

    //@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
    @PreAuthorize("hasAnyRole(@authorizedRoles.getGetservicestatus())")
    @GetMapping(path = "/s")
    @Operation(summary = "Service status 1", description = "Service status 1", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Service is running...", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> status1() {
        Date d = new Date();
        return ResponseEntity.status(HttpStatus.OK).body("Service is running... "+d.toString());
    }

    @PostMapping(path = "/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Initialization", description = "Initialization", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Initialization successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> init(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.init(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    @PostMapping(path = "/match", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Match", description = "Match", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Match successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> match(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.match(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    @PostMapping(path = "/check-quality", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Check quality", description = "Check quality", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Check successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> checkQuality(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.checkQuality(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    @Operation(summary = "extract-template", description = "extract-template", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Extract successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> extractTemplate(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.extractTemplate(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    @PostMapping(path = "/convert-format", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Convert format", description = "Convert format", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Convert successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> convertFormat(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.convertFormat(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    @PostMapping(path = "/segment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Segment", description = "Segment", tags = { "Sdk" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "201", description = "Segment successful", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<String> segment(
            @Validated @RequestBody(required = true) RequestDto request,
            @ApiIgnore Errors errors) {
        ResponseDto responseDto = generateResponseTemplate(request.getVersion());
        try {
            responseDto.setVersion(request.getVersion());
            BioSdkServiceProvider bioSdkServiceProviderImpl = null;
            bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
            responseDto.setResponse(bioSdkServiceProviderImpl.segment(request));
        } catch (BioSDKException e) {
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "BioSDKException: ", e.getMessage());
            ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
            responseDto.getErrors().add(errorDto);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
    }

    private ResponseDto generateResponseTemplate(String version){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setVersion(version);
        responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
        responseDto.setErrors(new ArrayList<ErrorDto>());
        responseDto.setResponse("");
        return responseDto;
    }

    private String getVersion(String request) throws BioSDKException{
        JSONParser parser = new JSONParser();
        try {
            JSONObject js = (JSONObject) parser.parse(request);
            return js.get("version").toString();
        } catch (ParseException e) {
            throw new BioSDKException(ErrorMessages.UNCHECKED_EXCEPTION.toString(), e.getMessage());
        }
    }
}
