package com.tabram.sudokusolver.controller;

import com.tabram.sudokusolver.dto.SudokuObjectDto;
import com.tabram.sudokusolver.model.SudokuObject;
import com.tabram.sudokusolver.service.*;
import com.tabram.sudokusolver.validation.CompareWithTempSudokuBoardValidation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolveController.class)
class SolveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SudokuObjectService sudokuObjectService;
    @MockBean
    private SudokuSolveService<SudokuObjectDto> sudokuSolveService;
    @MockBean
    private BoardValueManipulationService<SudokuObjectDto> boardValueManipulationService;
    @MockBean
    private MapperService mapperService;
    @MockBean
    private TempSudokuObjectService tempSudokuObjectService;
    @MockBean
    private CompareWithTempSudokuBoardValidation compareWithTempSudokuBoardValidation;

    @Nested
    class SolveAll {
        @Test
        void validInput_AndSolvedBoardIsInTemp_Return302() throws Exception {
            SudokuObjectDto sudokuObjectDtoTest = new SudokuObjectDto();
            Integer[][] boardNull = {
                    {null, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            sudokuObjectDtoTest.setBoard(boardNull);
            sudokuObjectDtoTest.setSudokuSize(9);
            sudokuObjectDtoTest.setQuantityBoxesWidth(3);
            sudokuObjectDtoTest.setQuantityBoxesHeight(3);
            Integer[][] boardZero = {
                    {9, 7, 4, 8, 6, 1, 3, 5, 2},
                    {3, 6, 1, 5, 9, 2, 8, 4, 7},
                    {8, 5, 2, 4, 3, 7, 9, 6, 1},
                    {1, 2, 7, 3, 8, 6, 5, 9, 4},
                    {6, 3, 9, 2, 4, 5, 7, 1, 8},
                    {5, 4, 8, 1, 7, 9, 2, 3, 6},
                    {4, 9, 6, 7, 2, 3, 1, 8, 5},
                    {2, 8, 5, 9, 1, 4, 6, 7, 3},
                    {7, 1, 3, 6, 5, 8, 4, 2, 9}
            };
            SudokuObject sudokuObjectTest = new SudokuObject(boardZero, 9, 3, 3);
            when(tempSudokuObjectService.getSudokuObject()).thenReturn(sudokuObjectTest);
            when(compareWithTempSudokuBoardValidation.compare(sudokuObjectDtoTest)).thenReturn(true);

            mockMvc.perform(put("/solve-all")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDtoTest))
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection())
                    .andDo(print());

            verify(boardValueManipulationService, times(1)).changeNullToZeroOnBoard(sudokuObjectDtoTest);
            verify(sudokuObjectService, times(1)).setSudokuObject(sudokuObjectTest);
            verify(tempSudokuObjectService, times(1)).setSudokuObject(null);
            verify(sudokuSolveService, never()).solveBoard(sudokuObjectDtoTest);
            verify(sudokuObjectService, never()).setSudokuObject(mapperService.mapperToSudokuBoardObject(sudokuObjectDtoTest));
        }

        @Test
        void validInput_Return302() throws Exception {
            SudokuObjectDto sudokuObjectDtoTest = new SudokuObjectDto();
            Integer[][] boardNull = {
                    {null, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            sudokuObjectDtoTest.setBoard(boardNull);
            sudokuObjectDtoTest.setSudokuSize(9);
            sudokuObjectDtoTest.setQuantityBoxesWidth(3);
            sudokuObjectDtoTest.setQuantityBoxesHeight(3);
            when(compareWithTempSudokuBoardValidation.compare(sudokuObjectDtoTest)).thenReturn(false);

            mockMvc.perform(put("/solve-all")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDtoTest))
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection())
                    .andDo(print());

            verify(boardValueManipulationService, times(1)).changeNullToZeroOnBoard(sudokuObjectDtoTest);
            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(tempSudokuObjectService, never()).setSudokuObject(null);
            verify(sudokuSolveService, times(1)).solveBoard(sudokuObjectDtoTest);
            verify(sudokuObjectService, times(1)).setSudokuObject(mapperService.mapperToSudokuBoardObject(sudokuObjectDtoTest));
        }

        @Test
        void invalidInput_NumberOutOfRange_Return302_AndErrorMsg() throws Exception {
            Integer[][] boardNull = {
                    {12, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            SudokuObjectDto sudokuObjectDto = new SudokuObjectDto();
            sudokuObjectDto.setBoard(boardNull);
            sudokuObjectDto.setSudokuSize(9);
            sudokuObjectDto.setQuantityBoxesHeight(3);
            sudokuObjectDto.setQuantityBoxesWidth(3);

            mockMvc.perform(put("/solve-all")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDto))
                    .andExpect(model().attributeHasErrors("sudokuObject"))
                    .andExpect(model().errorCount(1))
                    .andExpect(status().isOk())
                    .andDo(print());

            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(sudokuObjectService, never()).setSudokuObject(any());
            verify(tempSudokuObjectService, never()).setSudokuObject(any());
            verify(sudokuSolveService, never()).solveBoard(any());
        }

        @Test
        void invalidInput_NumberOutOfRange_AndInvalidPlacement_Return302_AndErrorMsg() throws Exception {
            Integer[][] boardNull = {
                    {12, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, 2}
            };
            SudokuObjectDto sudokuObjectDto = new SudokuObjectDto();
            sudokuObjectDto.setBoard(boardNull);
            sudokuObjectDto.setSudokuSize(9);
            sudokuObjectDto.setQuantityBoxesHeight(3);
            sudokuObjectDto.setQuantityBoxesWidth(3);

            mockMvc.perform(put("/solve-all")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDto))
                    .andExpect(model().attributeHasErrors("sudokuObject"))
                    .andExpect(model().errorCount(2))
                    .andExpect(status().isOk())
                    .andDo(print());

            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(sudokuObjectService, never()).setSudokuObject(any());
            verify(tempSudokuObjectService, never()).setSudokuObject(any());
            verify(sudokuSolveService, never()).solveBoard(any());
        }

    }

    @Nested
    class SolveCell {
        @Test
        void validInput_AndSolvedBoardIsInTemp_Return302() throws Exception {
            SudokuObjectDto sudokuObjectDtoTest = new SudokuObjectDto();
            Integer[][] boardNull = {
                    {null, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            sudokuObjectDtoTest.setBoard(boardNull);
            sudokuObjectDtoTest.setSudokuSize(9);
            sudokuObjectDtoTest.setQuantityBoxesWidth(3);
            sudokuObjectDtoTest.setQuantityBoxesHeight(3);
            // Sudoku from repository
            SudokuObject sudokuObjectRepoTest = new SudokuObject(boardNull, 9, 3, 3);
            //Sudoku from TempSudokuObject
            Integer[][] boardZero = {
                    {9, 7, 4, 8, 6, 1, 3, 5, 2},
                    {3, 6, 1, 5, 9, 2, 8, 4, 7},
                    {8, 5, 2, 4, 3, 7, 9, 6, 1},
                    {1, 2, 7, 3, 8, 6, 5, 9, 4},
                    {6, 3, 9, 2, 4, 5, 7, 1, 8},
                    {5, 4, 8, 1, 7, 9, 2, 3, 6},
                    {4, 9, 6, 7, 2, 3, 1, 8, 5},
                    {2, 8, 5, 9, 1, 4, 6, 7, 3},
                    {7, 1, 3, 6, 5, 8, 4, 2, 9}
            };
            SudokuObject sudokuObjectTempTest = new SudokuObject(boardZero, 9, 3, 3);
            int i = 1;
            int j = 2;
            when(compareWithTempSudokuBoardValidation.compare(sudokuObjectDtoTest)).thenReturn(true);
            when(tempSudokuObjectService.getSudokuObject()).thenReturn(sudokuObjectTempTest);
            when(sudokuObjectService.getSudokuObject()).thenReturn(sudokuObjectRepoTest);

            mockMvc.perform(put("/solve-cell")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDtoTest)
                            .param("solveCell", "cell[1][2]"))
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection())
                    .andDo(print());

            verify(tempSudokuObjectService, times(1)).getSudokuObject();
            verify(sudokuObjectService, times(1)).getSudokuObject();
            verify(sudokuSolveService, never()).solveBoard(sudokuObjectDtoTest);
            verify(mapperService, never()).mapperToSudokuBoardObject(sudokuObjectDtoTest);
            verify(tempSudokuObjectService, never()).setSudokuObject(sudokuObjectTempTest);
        }

        @Test
        void validInput_Return302() throws Exception {
            SudokuObjectDto sudokuObjectDtoTest = new SudokuObjectDto();
            Integer[][] boardNull = {
                    {null, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            sudokuObjectDtoTest.setBoard(boardNull);
            sudokuObjectDtoTest.setSudokuSize(9);
            sudokuObjectDtoTest.setQuantityBoxesWidth(3);
            sudokuObjectDtoTest.setQuantityBoxesHeight(3);
            //Sudoku from TempSudokuObject
            Integer[][] boardZero = {
                    {9, 7, 4, 8, 6, 1, 3, 5, 2},
                    {3, 6, 1, 5, 9, 2, 8, 4, 7},
                    {8, 5, 2, 4, 3, 7, 9, 6, 1},
                    {1, 2, 7, 3, 8, 6, 5, 9, 4},
                    {6, 3, 9, 2, 4, 5, 7, 1, 8},
                    {5, 4, 8, 1, 7, 9, 2, 3, 6},
                    {4, 9, 6, 7, 2, 3, 1, 8, 5},
                    {2, 8, 5, 9, 1, 4, 6, 7, 3},
                    {7, 1, 3, 6, 5, 8, 4, 2, 9}
            };
            SudokuObject sudokuObjectTempTest = new SudokuObject(boardZero, 9, 3, 3);
            int i = 1;
            int j = 2;
            when(compareWithTempSudokuBoardValidation.compare(sudokuObjectDtoTest)).thenReturn(false);
            when(tempSudokuObjectService.getSudokuObject()).thenReturn(sudokuObjectTempTest);
            when(sudokuObjectService.getSudokuObject()).thenReturn(sudokuObjectTempTest);

            mockMvc.perform(put("/solve-cell")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDtoTest)
                            .param("solveCell", "cell[1][2]"))
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection())
                    .andDo(print());

            verify(sudokuSolveService, times(1)).solveBoard(sudokuObjectDtoTest);
            verify(mapperService, times(1)).mapperToSudokuBoardObject(sudokuObjectDtoTest);
            verify(tempSudokuObjectService, times(1)).setSudokuObject(mapperService.mapperToSudokuBoardObject(sudokuObjectDtoTest));
            verify(tempSudokuObjectService, times(1)).getSudokuObject();
            verify(sudokuObjectService, times(1)).getSudokuObject();
        }

        @Test
        void invalidInput_NumberOutOfRange_Return302_AndErrorMsg() throws Exception {
            Integer[][] boardNull = {
                    {12, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            SudokuObjectDto sudokuObjectDto = new SudokuObjectDto();
            sudokuObjectDto.setBoard(boardNull);
            sudokuObjectDto.setSudokuSize(9);
            sudokuObjectDto.setQuantityBoxesHeight(3);
            sudokuObjectDto.setQuantityBoxesWidth(3);

            mockMvc.perform(put("/solve-cell")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDto)
                            .param("solveCell", "cell[1][2]"))
                    .andExpect(model().attributeHasErrors("sudokuObject"))
                    .andExpect(model().errorCount(1))
                    .andExpect(status().isOk())
                    .andDo(print());

            verify(sudokuSolveService, never()).solveBoard(any());
            verify(mapperService, never()).mapperToSudokuBoardObject(any());
            verify(tempSudokuObjectService, never()).setSudokuObject(any());
            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(sudokuObjectService, never()).getSudokuObject();
        }

        @Test
        void invalidInput_NumberOutOfRange_AndInvalidPlacement_Return302_AndErrorMsg() throws Exception {
            Integer[][] boardNull = {
                    {12, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, 2}
            };
            SudokuObjectDto sudokuObjectDto = new SudokuObjectDto();
            sudokuObjectDto.setBoard(boardNull);
            sudokuObjectDto.setSudokuSize(9);
            sudokuObjectDto.setQuantityBoxesHeight(3);
            sudokuObjectDto.setQuantityBoxesWidth(3);

            mockMvc.perform(put("/solve-cell")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDto)
                            .param("solveCell", "cell[1][2]"))
                    .andExpect(model().attributeHasErrors("sudokuObject"))
                    .andExpect(model().errorCount(2))
                    .andExpect(status().isOk())
                    .andDo(print());

            verify(sudokuSolveService, never()).solveBoard(any());
            verify(mapperService, never()).mapperToSudokuBoardObject(any());
            verify(tempSudokuObjectService, never()).setSudokuObject(any());
            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(sudokuObjectService, never()).getSudokuObject();
        }

        @Test
        void invalidInput_cellNotSelected_Return302_AndErrorMsg() throws Exception {
            Integer[][] boardNull = {
                    {null, null, 4, null, 6, null, null, null, 2},
                    {3, null, null, 5, null, null, null, null, 7},
                    {null, null, null, null, null, null, null, null, null},
                    {1, null, null, null, 8, null, null, null, null},
                    {null, 3, null, null, 4, null, 7, null, 8},
                    {5, null, null, null, 7, null, null, null, 6},
                    {null, null, null, null, null, null, 1, 8, null},
                    {2, null, null, 9, null, null, null, null, 3},
                    {null, 1, null, 6, null, null, null, 2, null}
            };
            SudokuObjectDto sudokuObjectDto = new SudokuObjectDto();
            sudokuObjectDto.setBoard(boardNull);
            sudokuObjectDto.setSudokuSize(9);
            sudokuObjectDto.setQuantityBoxesHeight(3);
            sudokuObjectDto.setQuantityBoxesWidth(3);

            mockMvc.perform(put("/solve-cell")
                            .contentType(MediaType.TEXT_HTML)
                            .characterEncoding("UTF-8")
                            .flashAttr("sudokuObject", sudokuObjectDto)
                            .param("solveCell", "notSelected"))
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.flash().attributeCount(1))
                    .andDo(print());

            verify(sudokuSolveService, never()).solveBoard(any());
            verify(mapperService, never()).mapperToSudokuBoardObject(any());
            verify(tempSudokuObjectService, never()).setSudokuObject(any());
            verify(tempSudokuObjectService, never()).getSudokuObject();
            verify(sudokuObjectService, never()).getSudokuObject();
        }
    }
}