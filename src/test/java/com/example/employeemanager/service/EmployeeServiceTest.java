package com.example.employeemanager.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.repository.EmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeServiceTest {

    private EmployeeRepository employeeRepository;
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    @Test
    void testSaveEmployee() {

        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@test.com");

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee saved = employeeService.saveEmployee(employee);

        assertNotNull(saved);
        assertEquals("John", saved.getFirstName());

        verify(employeeRepository).save(employee);
    }

    @Test
    void testGetAllEmployees() {

        Employee e1 = new Employee();
        Employee e2 = new Employee();

        when(employeeRepository.findAll())
                .thenReturn(Arrays.asList(e1, e2));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(2, employees.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void testGetEmployeeById() {

        Employee employee = new Employee();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testDeleteEmployee() {

        employeeService.deleteEmployeeById(1L);

        verify(employeeRepository).deleteById(1L);
    }
}