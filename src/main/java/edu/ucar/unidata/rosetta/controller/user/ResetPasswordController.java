/*
 * Copyright (c) 2012-2019 University Corporation for Atmospheric Research/Unidata.
 * See LICENSE for license information.
 */

package edu.ucar.unidata.rosetta.controller.user;

import edu.ucar.unidata.rosetta.domain.user.User;
import edu.ucar.unidata.rosetta.exceptions.RosettaUserException;
import edu.ucar.unidata.rosetta.service.user.UserManager;
import edu.ucar.unidata.rosetta.service.validators.user.PasswordValidator;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller to reset a user's password.
 */
@Controller
public class ResetPasswordController {

    private static final Logger logger = Logger.getLogger(ResetPasswordController.class);
    private final PasswordValidator passwordValidator;
    @Resource(name = "userManager")
    private UserManager userManager;

    /**
     * Creates this controller class.
     *
     * @param passwordValidator The validator used to validate User objects associated with this
     *                          controller class.
     */
    @Autowired
    public ResetPasswordController(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
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
        binder.setValidator(passwordValidator);
    }

    /**
     * Accepts a POST request to edit an existing user's password.
     * <p>
     * View is the user view.  The model contains: 1) the User object with the changed password (if
     * successful) displayed in the view via jspf; or 2) the web form to edit the User's password if
     * there are validation errors with the user input.
     * <p>
     * Only the User/owner and Users with a role of 'ROLE_ADMIN' are allowed to edit the User
     * account.
     *
     * @param userName The 'userName' as provided by @PathVariable.
     * @param user     The User to edit.
     * @param result   The BindingResult for error handling.
     * @param model    The Model used by the View.
     * @return The redirect to the needed View.
     * @throws RosettaUserException If unable to locate user.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userName == authentication.name")
    @RequestMapping(value = "/user/password/{userName}", method = RequestMethod.POST)
    public ModelAndView processUserPasswordReset(@PathVariable String userName, @Valid User user,
                                                 BindingResult result, Model model) throws RosettaUserException {
        logger.debug("Processing submitted reset user password form data.");
        if (result.hasErrors()) {
            logger.debug(
                    "Validation errors detected in reset user password form data. Returning user to form view.");
            model.addAttribute("action", "resetPassword");
            model.addAttribute("user", user);
            return new ModelAndView("user");
        } else {
            logger.debug(
                    "No validation errors detected in reset user password form data. Proceeding with password reset.");
            User u = userManager.lookupUser(userName);
            u.setPassword(user.getPassword());
            userManager.updatePassword(u);
            return new ModelAndView(new RedirectView("/user/view/" + userName, true));
        }
    }

    /**
     * Accepts a GET request to edit an existing user's password.
     * <p>
     * The view is the user view.  The model contains the User object with the password to edit and
     * the information which will be loaded and displayed in the view via jspf.
     * <p>
     * Only the User/owner and Users with a role of 'ROLE_ADMIN' are allowed to edit the User's
     * password.
     *
     * @param userName The 'userName' as provided by @PathVariable.
     * @param model    The Model used by the View.
     * @return The path for the ViewResolver.
     * @throws RosettaUserException If unable to locate user.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userName == authentication.name")
    @RequestMapping(value = "/user/password/{userName}", method = RequestMethod.GET)
    public String resetUserPassword(@PathVariable String userName, Model model)
            throws RosettaUserException {
        logger.debug("Get reset user password form.");
        User user = userManager.lookupUser(userName);
        model.addAttribute("action", "resetPassword");
        model.addAttribute("user", user);
        return "user";
    }
}
