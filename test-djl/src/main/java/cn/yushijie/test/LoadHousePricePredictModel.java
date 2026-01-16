package cn.yushijie.test;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @ClassName LoadHousePricePredictModel
 * @Description
 * @Author yu155
 * @Date 2026/1/13 17:04
 * @Version 1.0.0
 */
public class LoadHousePricePredictModel {
    public static void main(String[] args) {
        // 1. 定义输入输出类型及转换逻辑
        Translator<float[], Float> translator = new Translator<float[], Float>() {
            // 归一化 输入转化
            @Override
            public NDList processInput(TranslatorContext ctx, float[] input) {
                // 将Java浮点数组转换为DJL NDArray（模型输入张量）
                // 创建形状为 [1, 5] 的张量 需要结合定义的模型来设置张量形状
                NDManager manager = ctx.getNDManager();
                NDArray array = manager.create(input).reshape(1, 5);
                return new NDList(array);
            }

            // 输出转化
            @Override
            public Float processOutput(TranslatorContext ctx, NDList list) {
                // 将模型输出的NDList转换为Java Float
                // 假设模型输出是单个标量
                return list.head().getFloat();
            }

            @Override
            public Batchifier getBatchifier() {
                // 如果你的推理不支持批量处理，返回null
                return null;
            }
        };

        // 2. 构建模型标准：指定模型文件位置和翻译器
        Criteria<float[], Float> criteria = Criteria.builder()
                .setTypes(float[].class, Float.class) // 定义Java输入/输出类型
                .optModelPath(Paths.get("D:\\house_price_model_traced.pt")) // 指定模型文件路径
                .optTranslator(translator) // 应用自定义转换器
                .optEngine("PyTorch") // 指定引擎
//                .optModelName("your_model_name") // 可选，指定模型名[citation:1]
                .build();

        // 3. 加载模型并进行推理
        try (ZooModel<float[], Float> model = criteria.loadModel();
             Predictor<float[], Float> predictor = model.newPredictor()) {

            // 准备输入数据（这里是一个示例数组，实际需要替换为你的数据）
            float[] inputData = new float[]{100f, 5f, 3f, 1f, 2f};
            // ... 填充inputData ...

            // 执行预测
            Float output = predictor.predict(inputData);
            System.out.println("预测结果: " + output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
