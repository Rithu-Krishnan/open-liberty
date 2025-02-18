/*******************************************************************************
 * Copyright (c) 2019, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.microprofile.reactive.streams.test;

import static org.hamcrest.Matchers.is;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.microprofile.reactive.streams.operators.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.Test;

import io.openliberty.microprofile.reactive.streams.test.utils.CompletionStageResult;

public class CompletionStageTest extends AbstractReactiveUnitTest {

    @Test
    public void test() {
        CompletableFuture<Void> latch = new CompletableFuture<>();
        CompletionRunner<Optional<Integer>> runner = ReactiveStreams.of(1, 2, 3, 4, 5).map(x -> x * 2)
                        .map(waitFor(latch)).reduce((x, y) -> x + y);
        CompletionStage<Optional<Integer>> cs = runner.run();

        latch.complete(null);
        CompletionStageResult.from(cs.thenApply(Optional::get)).assertResult(is(30));
    }

    private <T> Function<T, T> waitFor(Future<?> latch) {
        return (t) -> {
            try {
                latch.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return t;
        };
    }

}
