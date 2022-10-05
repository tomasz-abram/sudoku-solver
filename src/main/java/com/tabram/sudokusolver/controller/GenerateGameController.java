package com.tabram.sudokusolver.controller;

import com.tabram.sudokusolver.model.SudokuObject;
import com.tabram.sudokusolver.repository.SudokuObjectRepository;
import com.tabram.sudokusolver.repository.TempSudokuObject;
import com.tabram.sudokusolver.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GenerateGameController {
    private static final String HOME = "redirect:/";
    private final GenerateSudokuGameService generateSudokuGameService;
    private final ClearBoardService clearBoardService;
    private final SudokuObjectRepository sudokuObjectRepository;
    private final BoardValueManipulation boardValueManipulation;
    private final SudokuSolveService sudokuSolveService;
    private final CopyObjectService copyObjectService;
    private final TempSudokuObject tempSudokuObject;

    public GenerateGameController(GenerateSudokuGameService generateSudokuGameService, ClearBoardService clearBoardService, SudokuObjectRepository sudokuObjectRepository, BoardValueManipulation boardValueManipulation, SudokuSolveService sudokuSolveService, CopyObjectService copyObjectService, TempSudokuObject tempSudokuObject) {
        this.generateSudokuGameService = generateSudokuGameService;
        this.clearBoardService = clearBoardService;
        this.sudokuObjectRepository = sudokuObjectRepository;
        this.boardValueManipulation = boardValueManipulation;
        this.sudokuSolveService = sudokuSolveService;
        this.copyObjectService = copyObjectService;
        this.tempSudokuObject = tempSudokuObject;
    }


    @GetMapping("/generate-easy-game")
    public String generateGame(@RequestParam String level) {
        int percent;
        switch (level) {
            case "Easy":
                percent = 55;
                break;
            case "Medium":
                percent = 63;
                break;
            case "Hard":
                percent = 72;
                break;
            default:
                percent = 0;
        }
        SudokuObject sudokuObject = sudokuObjectRepository.getSudokuObject();
        clearBoardService.clearBoard(sudokuObject);
        boardValueManipulation.changeNullToZeroOnBoard(sudokuObject);
        generateSudokuGameService.generate(sudokuObject);
        sudokuSolveService.solveBoard(sudokuObject);
        tempSudokuObject.setSudokuObject(copyObjectService.deepCopy(sudokuObject));
        generateSudokuGameService.randomCleanBoard(sudokuObject, percent);
        return HOME;
    }
}