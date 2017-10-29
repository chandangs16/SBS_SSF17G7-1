package edu.asu.sbs.controllers;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.asu.sbs.model.Account;
import edu.asu.sbs.model.ExternalUser;
import edu.asu.sbs.model.ExternalUserSearch;
import edu.asu.sbs.model.InternalUser;
import edu.asu.sbs.model.ModifiedUser;
import edu.asu.sbs.model.SystemLog;
import edu.asu.sbs.model.Transaction;
import edu.asu.sbs.services.AccountService;
import edu.asu.sbs.services.ExternalUserService;
import edu.asu.sbs.services.InternalUserService;
import edu.asu.sbs.services.ModifiedUserService;
import edu.asu.sbs.services.SystemLogService;
import edu.asu.sbs.services.TransactionService;

@Controller
public class InternalUserController {

	@Autowired
	private SystemLogService systemLogService;
	
	@Autowired
	private InternalUserService internalUserService;
	
	@Autowired
	private ExternalUserService externalUserService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ModifiedUserService modifiedUserService;
	
	@Autowired
	private TransactionService transactionService;
	
	@ModelAttribute
	public ExternalUserSearch getExternalUser() {
		return new ExternalUserSearch();
	}
	
	/********************* ADMIN ************************************/
	/** authenticates and gives admin home */
	@RequestMapping(value="/admin/home",method = RequestMethod.GET)
	public String getAdminHome() {
		return "adminhome";
	}
	
	/** Lists all the System logs */
	@RequestMapping(value="/admin/systemlogs",method=RequestMethod.GET)
	public String getSystemLogs(Model model) {
		System.out.println("Fetching System logs..");
		List<SystemLog> systemLogList = systemLogService.getSystemLog();
		for(SystemLog log : systemLogList) {
			System.out.println(log.getFirstName());
		}
		model.addAttribute("systemLog", new SystemLog());
		model.addAttribute("systemLogList", systemLogList);
		model.addAttribute("msg", " Welcome Santosh");
		return "systemlog";
	}
	
	/** Add new System Log to the DB 
	 *  call the system log method
	 *  where its required. 
	 *  This is just for testing purpose
	 * */
	public String addSystemLog(@ModelAttribute("systemLog") SystemLog systemLog) {
		return "redirect:/systemlog";
	}
	
	
	/** add, delete, update, find internal users **/
	
	/** lists all the internal users for admin **/
	@RequestMapping(value="/admin/employee-list")
	public ModelAndView listOfEmployees() {
		ModelAndView modelAndView = new ModelAndView("employeelist");
		System.out.println("All Users Page");
		@SuppressWarnings("rawtypes")
		List employees = internalUserService.findAllUsers();
		modelAndView.addObject("employees", employees);
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/employee-add")
	public ModelAndView addInternalUserPage() {
		System.out.println("Add new user page");
		ModelAndView modelAndView = new ModelAndView("admin-adduser");
		modelAndView.addObject("employee", "new");
		modelAndView.addObject("employeeForm", new InternalUser());
		return modelAndView;
	}
	
	// show update form
	@RequestMapping(value = "/admin/employee-update/{id}", method = RequestMethod.GET)
	public ModelAndView showUpdateEmployeeForm(@PathVariable("id") int id, Model model) {
		System.out.println("Updating user");
		ModelAndView modelAndView = new ModelAndView("admin_adduser");
		InternalUser internalUser = internalUserService.findUserById(id);
		modelAndView.addObject("employee", "exists");
		model.addAttribute("employeeForm", internalUser);
		return modelAndView;

	}
	
	@RequestMapping(value="/admin/employee-add-modify")
	public ModelAndView addingInternalUser(@ModelAttribute InternalUser internalUser) {
		System.out.println("Adding new user and redirecting" + internalUser.getPasswordHash());
		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		java.text.SimpleDateFormat sdf = 
		     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		internalUser.setCreationDate(date);
		internalUser.setLastLogin(date);
		
		if( internalUser.getEmployeeId() == 0) {
			System.out.println("Add User" + internalUser.getEmployeeId());
			internalUserService.addUser(internalUser);	
		}else {
			System.out.println("Modify User" + internalUser.getEmployeeId());
			internalUserService.updateUser(internalUser);
		}
		ModelAndView modelAndView = new ModelAndView("employeelist");
		System.out.println("All Users Page");
		@SuppressWarnings("rawtypes")
		List employees = internalUserService.findAllUsers();
		modelAndView.addObject("employees", employees);
		return modelAndView;
	}
	

	@RequestMapping(value="/admin/employee-delete/{id}", method=RequestMethod.POST)
	public ModelAndView deleteInternalUser(@PathVariable Integer id) {
		System.out.println("Deleting the user with id"+ id);
		internalUserService.deleteUser(id);
		ModelAndView modelAndView = new ModelAndView("employeelist");
		System.out.println("All Users Page");
		@SuppressWarnings("rawtypes")
		List employees = internalUserService.findAllUsers();
		modelAndView.addObject("employees", employees);
		return modelAndView;
	}
		
	/** admin - request pending **/
	@RequestMapping(value="/admin/requests-pending", method = RequestMethod.GET)
	public ModelAndView getRequestsPending() {
		ModelAndView modelAndView = new ModelAndView("pendingrequests");
		List<ModifiedUser> users = modifiedUserService.findAllUsers(1);
		modelAndView.addObject("users", users);
		return modelAndView; 
	}
	
	/** admin - approves **/
	@RequestMapping(value="/admin/approve/{id}", method = RequestMethod.GET)
	public ModelAndView approveInternalUserRequests(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView("pendingrequests");
		// get the modified user object
		ModifiedUser modUser = modifiedUserService.findUserById(id);
		System.out.println("modUser::"+ modUser.getFirstName());
		//update the modified user object to approved
		modUser.setStatus(1);
		modUser.setStatus_quo("approved");;
		modifiedUserService.updateUser(modUser);
		internalUserService.updateUser(modUser);
		System.out.println("Status has been updated and object modified");
		List<ModifiedUser> users = modifiedUserService.findAllUsers(1);
		modelAndView.addObject("users", users);
		modelAndView.addObject("msg","Approved and the account has been modified.");
		return modelAndView; 
	}
	
	/** admin - rejects **/
	@RequestMapping(value="/admin/decline/{id}", method = RequestMethod.GET)
	public ModelAndView declineInternalUserRequests(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView("pendingrequests");
		// get the modified user object
		ModifiedUser modUser = modifiedUserService.findUserById(id);
		System.out.println("modUser::"+ modUser.getFirstName());
		//update the modified user object to approved
		modUser.setStatus(1);
		modUser.setStatus_quo("declined");
		modifiedUserService.updateUser(modUser);
		//internalUserService.updateUser(modUser);
		System.out.println("Status has been updated and object modified");
		List<ModifiedUser> users = modifiedUserService.findAllUsers(1);
		modelAndView.addObject("users", users);
		modelAndView.addObject("msg","Approved and the account has been modified.");
		return modelAndView; 
	}
	/** ----------- REGULAR EMPLOYEE----------------**/
	
	/** customer search page **/
	@RequestMapping(value="/employee/customer-transaction", method = RequestMethod.GET)
	public ModelAndView returnTransactionPage() {
		ModelAndView modelAndView  = new ModelAndView("customersearch");
		modelAndView.addObject("externalUser", getExternalUser());
		return modelAndView;
	}
	
	/** get accounts from the customer/merchant for transfer */
	@RequestMapping(value="/employee/customer-transaction", method = RequestMethod.POST)
	public ModelAndView returnTransactionPage(@ModelAttribute ExternalUserSearch customer) {
		ModelAndView modelAndView  = new ModelAndView("internal-usertransaction");
		System.out.println("Fetching the user details " + customer.getCustomerId());
		ExternalUser externalUser  = externalUserService.findUserById(customer.getCustomerId()); 
		List<Account> accounts = accountService.getAccountByCustomerId(externalUser.getCustomerId());
		modelAndView.addObject("accounts", accounts);
		modelAndView.addObject("user", externalUser);
		return modelAndView;
	}
	
	/** process the transaction **/
	@RequestMapping(value="/employee/transaction/process", method = RequestMethod.POST)
	public ModelAndView processTransaction() {
		
		return new ModelAndView();
	}
	
	/** tier-1 profile **/
	@RequestMapping(value="/employee/profile",method=RequestMethod.GET)
	public ModelAndView getEmployeeProfile() {
		ModelAndView modelAndView = new ModelAndView("employee-profile");
		modelAndView.addObject("employeeForm", internalUserService.findByUserName());
		return modelAndView;
		
	}
	
	/** tier-1 profile update */
	@RequestMapping(value="/employee/modify-profile",method=RequestMethod.POST)
	public ModelAndView addModifiedProfile(@ModelAttribute InternalUser internalUser) {
		ModelAndView modelAndView = new ModelAndView("employee-profile");
		System.out.println("User name to be modified ::" + internalUser.getFirstName());
		ModifiedUser modUser = new ModifiedUser(internalUser.getEmployeeId(), internalUser.getFirstName(), 
				internalUser.getLastName(), internalUser.getEmailId(), internalUser.getPhoneNumber(), 
				internalUser.getAddress(), 0, "pending",1);
		modifiedUserService.addUser(modUser);
		modelAndView.addObject("msg", "Profile has been submitted for approval");
		return modelAndView;
	}
	
	@RequestMapping(value="/employee/home",method=RequestMethod.GET)
	public ModelAndView getEmployeeHomePage() {
		return new ModelAndView("employeehome");
	}
	
	/** show pending transactions **/
	@RequestMapping(value="/employee/pending-transactions",method=RequestMethod.GET)
	public ModelAndView getPendingTransactionsForRegular() {
		System.out.println("Fetching all pending transactions for customers");
		ModelAndView modelAndView = new ModelAndView("emp-pending-trans");
		List<Transaction> transactions = transactionService.getAllTransaction(1);
		modelAndView.addObject("transactions", transactions);
		return modelAndView;
	}
	
	/** approve non-critical pending( approved) transaction **/
	@RequestMapping(value="/employee/approve-transaction/{id}",method=RequestMethod.GET)
	public String approveCustomerPendingTrans(@PathVariable("id") int id){
		System.out.println("Approving the pending customer transactions");
		Transaction transaction = transactionService.get(id);
		transaction.setStatus(1);
		transaction.setStatus_quo("approved");
		transactionService.updateTransaction(transaction);
		// update the respective accounts to reflect changes
		// redirection not working
		return "redirect:/employee/pending-transactions";
	}
	
	/** approve non-critical pending( approved) transaction **/
	@RequestMapping(value="/employee/decline-transaction/{id}",method=RequestMethod.GET)
	public String declineCustomerPendingTrans(@PathVariable("id") int id){
		System.out.println("Declining the pending customer transactions");
		Transaction transaction = transactionService.get(id);
		transaction.setStatus(2);
		transaction.setStatus_quo("declined");
		transactionService.updateTransaction(transaction);
		// redirecting not working
		return "redirect:/employee/pending-transactions";
	}
	
	/** redirect to employee profile update **/
	@RequestMapping(value="/employee/pending-profile",method = RequestMethod.GET)
	public ModelAndView getCustomerProfilePending() {
		ModelAndView modelAndView = new ModelAndView("emp-pending-profile");
		List<ModifiedUser> modifiedUsers = modifiedUserService.findAllUsers(0);
		modelAndView.addObject("customers",modifiedUsers);
		return modelAndView;
	}
	
	/** approve the modified employee profile **/
	@RequestMapping(value="/employee/approve-profile/{id}",method=RequestMethod.GET)
	public String approveCustomerProfilePending(@PathVariable("id") int id){
		// get the modified user object
		ModifiedUser modUser = modifiedUserService.findUserById(id);
		System.out.println("modUser::"+ modUser.getFirstName());
		//update the modified user object to approved
		modUser.setStatus(1);
		modUser.setStatus_quo("approved");;
		modifiedUserService.updateUser(modUser);
		externalUserService.update(modUser);
		System.out.println("Status has been updated and object modified");
		return "redirect:/employee/pending-profile";
	}
	
	/** decline the modified employee profile **/
	@RequestMapping(value="/employee/decline-profile/{id}",method=RequestMethod.GET)
	public String declineCustomerProfilePending(@PathVariable("id") int id){
		ModifiedUser modUser = modifiedUserService.findUserById(id);
		System.out.println("modUser::"+ modUser.getFirstName());
		//update the modified user object to approved
		modUser.setStatus(2);
		modUser.setStatus_quo("declined");;
		modifiedUserService.updateUser(modUser);
		return "redirect:/employee/pending-profile";
	}
	
	
	/** add, delete, update, find external users **/
	
	/** lists all the external users for internal **/
	@RequestMapping(value="/employee/customer-list")
	public ModelAndView listOfCustomers() {
		ModelAndView modelAndView = new ModelAndView("customerlist");
		System.out.println("All Customers List");
		@SuppressWarnings("rawtypes")
		List<ExternalUser> customers = externalUserService.findAll();
		modelAndView.addObject("customers", customers);
		return modelAndView;
	}
	
	
	/** add external user customer/merchant **/
	@RequestMapping(value="/employee/customer-add", method = RequestMethod.GET)
	public ModelAndView addCustomerMerchantPage() {
		System.out.println("Add new customer/merchant page");
		ModelAndView modelAndView = new ModelAndView("int-addcustomer");
		modelAndView.addObject("customer", "new");
		modelAndView.addObject("customerForm", new ExternalUser());
		return modelAndView;
	}
	
	// show update form
	@RequestMapping(value = "/employee/customer-update/{id}", method = RequestMethod.GET)
	public ModelAndView showUpdateCustomerForm(@PathVariable("id") int id, Model model) {
		System.out.println("Updating Customer");
		ModelAndView modelAndView = new ModelAndView("int-addcustomer");
		ExternalUser externalUser = externalUserService.findUserById(id);
		System.out.println("Modifying user"+ externalUser.getFirstName());
		modelAndView.addObject("customer", "exists");
		model.addAttribute("customerForm", externalUser);
		return modelAndView;

	}
	/** adds or modify the customer data **/
/*	@RequestMapping(value="/employee/customer-add-modify")
	public String addingCustomer(@ModelAttribute ExternalUser externalUser) {
		System.out.println("Adding/Modifying new customer and redirecting" + externalUser.getPasswordHash());
		
		if( externalUser.getCustomerId() == 0) {
			System.out.println("Add User" + externalUser.getCustomerId());
			externalUserService.add(externalUser);	
		}else {
			System.out.println("Modify User" + externalUser.getCustomerId());
			externalUserService.update(externalUser);
		}
		return "redirect:/employee/customer-list";
	}*/
	
	/** MANAGER LIST **/
}