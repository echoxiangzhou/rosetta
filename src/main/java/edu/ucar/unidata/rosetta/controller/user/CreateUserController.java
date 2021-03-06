/*
 * Copyright (c) 2012-2019 University Corporation for Atmospheric Research/Unidata.
 * See LICENSE for license information.
 */

package edu.ucar.unidata.rosetta.controller.user;

import edu.ucar.unidata.rosetta.domain.user.User;
import edu.ucar.unidata.rosetta.exceptions.RosettaUserException;
import edu.ucar.unidata.rosetta.service.user.UserManager;
import edu.ucar.unidata.rosetta.service.validators.user.CreateUserValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Controller to handle user creation.
 */
@Controller
public class CreateUserController {

    private static final Logger logger = Logger.getLogger(CreateUserController.class);
    private final CreateUserValidator createUserValidator;
    @Resource(name = "userManager")
    private UserManager userManager;

    /**
     * Creates this controller class.
     *
     * @param createUserValidator The validator used to validate User objects associated with this
     *                            controller class.
     */
    @Autowired
    public CreateUserController(CreateUserValidator createUserValidator) {
        this.createUserValidator = createUserValidator;
    }

    /**
     * Accepts a GET request to create a new user.
     * <p>
     * The view is the user view.  The model contains a blank User object and action information which
     * will be loaded and displayed in the view via jspf.
     * <p>
     * Only users with a role of 'ROLE_ADMIN' are allowed to create new users using this method (See
     * register method for public access.)
     *
     * @param model The Model used by the view.
     * @return The path for the ViewResolver.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/create", method = RequestMethod.GET)
    public String createUser(Model model) {
        logger.debug("Get create user form.");
        // Create a form-backing object.
        User user = new User();
        model.addAttribute("action", "createUser");
        model.addAttribute("user", user);
        return "user";
    }

    /**
     * Initialize the WebDataBinder used for populating command and form object arguments.
     *
     * @param binder The WebDataBinder.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
        binder.setValidator(createUserValidator);
    }

    /**
     * Accepts a POST request to create a new user and persist it.
     * <p>
     * View is the user view.  The model contains: 1) the newly created User object (if successful)
     * displayed in the view via jspf; or 2) the web form to create a new user if there are validation
     * errors with the user input.
     * <p>
     * Only users with a role of 'ROLE_ADMIN' are allowed to create new users using this method (See
     * register method for public access.)
     *
     * @param user   The user to persist.
     * @param result The BindingResult for error handling.
     * @param model  The Model used by the view.
     * @return The redirect to the needed view.
     * @throws RosettaUserException If unable to create the new user.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public ModelAndView processUserCreation(@Valid User user, BindingResult result, Model model)
            throws RosettaUserException {
        logger.debug("Processing submitted create user form data.");
        // Check for validation errors.
        if (result.hasErrors()) {
            logger.debug(
                    "Validation errors detected in create user form data. Returning user to form view.");
            model.addAttribute("action", "createUser");
            return new ModelAndView("user");
        } else {
            logger.debug(
                    "No validation errors detected in create user form data. Proceeding with new user creation.");
            user = userManager.createUser(user);
            return new ModelAndView(new RedirectView("/user/view/" + user.getUserName(), true));
        }
    }

    /**
     * Accepts a POST request to create a new user via registration and persist it.
     * <p>
     * View is the user view.  The model contains: 1) the newly created User object (if successful)
     * displayed in the view via jspf;or 2) the web form to create a new user if there are validation
     * errors with the user input.
     * <p>
     * Anyone can perform user registration.
     *
     * @param user   The user to persist.
     * @param result The BindingResult for error handling.
     * @param model  The Model used by the view.
     * @return The redirect to the needed view.
     * @throws RosettaUserException If unable to process the user registration.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistration(@Valid User user, BindingResult result, Model model)
            throws RosettaUserException {
        logger.debug("Processing submitted new user registration form data.");
        // Check for validation errors.
        if (result.hasErrors()) {
            logger.debug(
                    "Validation errors detected in new user registration form data. Returning user to new user registration view.");
            model.addAttribute("action", "register");
            return new ModelAndView("user");
        } else {
            logger.debug(
                    "No validation errors detected in new user registration form data. Proceeding with new user regsitration.");
            userManager.createUser(user);
            return new ModelAndView(new RedirectView("/login", true));
        }
    }

    /**
     * Accepts a GET request to create a new user via registration.
     * <p>
     * The view is the user view.  The model contains a blank User object and action information which
     * will be loaded and displayed in the view via jspf.
     * <p>
     * Anyone can perform user registration.
     *
     * @param model The Model used by the View.
     * @return The path for the ViewResolver.
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        logger.debug("Get new user registration form.");
        // Create a form-backing object.
        User user = new User();
        model.addAttribute("action", "register");
        model.addAttribute("user", user);
        return "user";
    }

}
