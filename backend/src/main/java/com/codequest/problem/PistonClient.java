package com.codequest.problem;

import com.codequest.problem.dto.PistonRequest;
import com.codequest.problem.dto.PistonResponse;

public interface PistonClient {

    PistonResponse execute(PistonRequest request);
}
