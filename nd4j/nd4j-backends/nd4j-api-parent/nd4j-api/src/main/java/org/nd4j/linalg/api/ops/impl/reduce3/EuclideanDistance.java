/*******************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.nd4j.linalg.api.ops.impl.reduce3;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.BaseReduceFloatOp;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;

/**
 * Euclidean distance
 *
 * @author Adam Gibson
 */
public class EuclideanDistance extends BaseReduceFloatOp {
    public static final String OP_NAME = "euclidean";

    public EuclideanDistance(SameDiff sameDiff, SDVariable i_v, int[] dimensions) {
        super(sameDiff, i_v, dimensions);
    }

    public EuclideanDistance(SameDiff sameDiff, SDVariable i_v, SDVariable i_v2, int[] dimensions) {
        super(sameDiff, i_v, i_v2, dimensions);
    }

    public EuclideanDistance() {}



    public EuclideanDistance(INDArray x, INDArray y, int... dimensions) {
        super(x, y, dimensions);
        extraArgs = new Object[]{0.0f, 0.0f};
    }

    public EuclideanDistance(INDArray x, INDArray y, INDArray z, int... dimensions) {
        super(x, y, z,dimensions);
        extraArgs = new Object[]{0.0f, 0.0f};
    }

    public EuclideanDistance(INDArray x, INDArray y, boolean allDistances, int... dimensions) {
        this(x, y, null, allDistances, dimensions);
    }

    public EuclideanDistance(INDArray x, INDArray y, INDArray z, boolean allDistances, int... dimensions) {
        this(x, y, z, true, false, dimensions);
        this.isComplex = allDistances;
    }

    public EuclideanDistance(INDArray x, INDArray y, INDArray z, boolean newFormat, boolean keepDims, int... dimensions){
        super(x, y, z, newFormat, keepDims, dimensions);
        extraArgs = new Object[]{0.0f, 0.0f};
    }

    @Override
    public Type opType() {
        return Type.REDUCE3;
    }

    @Override
    public Type getOpType() {
        return opType();
    }

    @Override
    public int opNum() {
        return 1;
    }

    @Override
    public String opName() {
        return OP_NAME;
    }


    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v1) {
        //ddist(x,y)/dxi = (xi-yi)/dist(x,y)
        SDVariable euc = outputVariables()[0];
        SDVariable difference = larg().sub(rarg());
        SDVariable divBroadcastable;
        int origRank = Shape.rankFromShape(arg().getShape());   //TODO shape may not always be defined?
        divBroadcastable = f().reductionBroadcastableWithOrigShape(origRank, dimensions, i_v1.get(0).div(euc));

        SDVariable gradX = difference.mul(divBroadcastable);
        SDVariable gradY = f().neg(gradX);
        return Arrays.asList(gradX, gradY);
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " +  opName());

    }

    @Override
    public String tensorflowName() {
        throw new NoOpNameFoundException("No tensorflow op opName found for " +  opName());
    }

    @Override
    public DataType resultType() {
        return Nd4j.defaultFloatingPointType();
    }
}
