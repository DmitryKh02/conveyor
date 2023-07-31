package ru.Neoflex.conveyor.Controller.Advice;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.Neoflex.conveyor.Controller.ConveyorController;
import ru.Neoflex.conveyor.Exception.InvalidDataException;
import ru.Neoflex.conveyor.Exception.InvalidField;
import ru.Neoflex.conveyor.Service.ConveyorService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



@ExtendWith(SpringExtension.class)
@WebMvcTest(ExceptionConveyorHandler.class)
public class ExceptionConveyorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConveyorService conveyorService;

    @MockBean
    private ExceptionConveyorHandler exceptionConveyorHandler;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ConveyorController(conveyorService))
                .setControllerAdvice(exceptionConveyorHandler)
                .build();
    }


    @Test
    public void testInvalidDataException() throws Exception {
        // Создаем мок объект InvalidDataException с некоторыми неверными полями
        InvalidDataException invalidDataException = mock(InvalidDataException.class);
        List<InvalidField> invalidFields = new ArrayList<>();
        invalidFields.add(new InvalidField("Поле1", "Неверное значение"));
        invalidFields.add(new InvalidField("Поле2", "Еще одно неверное значение"));
        when(invalidDataException.getInvalidFields()).thenReturn(invalidFields);

    }
}
