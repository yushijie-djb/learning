# Pytorch

## 设计思想

Tensor -> Autogard -> nn.Module

高维数组 -》自动求导 -》神经网络

## 基础概念

### 模型训练

*模型训练本质上在干嘛？*

1. **模型给出预测（比如概率 0.8）**
2. **计算“错得有多离谱”（loss）**
3. **调整参数，让下次错得更少**

### Tensor

- ***标量、向量、矩阵都是张量，只是维度不同，Tensor包含多维数组，模型输入/输出都是张量***
- **维度变换**：
  - view：只能用于内存中连续存储的Tensor，对于不连续的Tensor应先将其连续化tensor.contiguous.view()
  - reshape：不要求Tensor在内存中是连续的，直接使用即可。
- **转置：**
  - self.W =
    [
      [1, 2, 3],
      [4, 5, 6]
    ]
  - self.W.T =
    [
      [1, 4],
      [2, 5],
      [3, 6]
    ]
- **类型：**
  - 设备类型（device）-- CUDA/CPU
  - 数据类型（dtype）-- bool/float/int
- **命名张量：**推荐使用维度的名称进行维度操作，这样可以避免重复计算Tensor每个维度的位置
- **Numpy：**可以与numpy数组转换，当某些场景下Tensor不支持的操作可以先转换为numpy进行处理，然后转回Tensor

### Shape

在每一个维度上的长度 

```python
标量：x = 5 → shape = ()

向量：x = [10, 20, 30] → shape = (3)

矩阵：

[
[1, 2, 3],
[4, 5, 6]
] → 
shape = (2, 3)

三维张量：

[
[[a, b],
[c, d]],

[[e, f],
[g, h]],

[[i, j],
[k, l]]
] → 
shape = (3, 2, 2)
```

### 反向传播

出了结果以后，倒着查“谁该为错误负责” 

在计算图上应用链式法则（*原理过于深奥，不做深入了解*）

用一次 backward，自动算出所有参数，“该往哪调、调多少”。

### 梯度

**torch.grad**

- 一句话抓住本质：往哪个方向改一点，结果变化最快。
- 梯度“数值大小”意味着什么？
  - 梯度大 → 稍微动一点，结果变化很大
  - 梯度小 → 动半天，结果几乎不变

**梯度爆炸问题**

- grad在反向传播过程中是累加的，因此每次进行反向传播之前需要把梯度清零

### AutoGrad

- **通俗理解**：拧水龙头(参数X) -》龙头出水(结果Y)，现在 关心的是 我把水龙头拧紧一点/松一点，水量会变化多少？auto_grad就是自动化的告诉你拧一点点，水量的变化情况

### 全连接层

每个输出神经元，都连接到每个输入神经元

```pyth
y1 = w11*x1 + w12*x2 + w13*x3
y2 = w21*x1 + w22*x2 + w23*x3
# x1 ->
# x2 -> 全连接 ↗ y1
# x3 ->       ↘ y2
```

**全连接层 = 一个参数矩阵 W + 一个偏置 b**

**y = Wx + b**（W和b都是可学习参数，***矩阵的乘法只看矩阵的形状是否满足要求，所以需要转置y = x @ self.W.T # A @ B 表示矩阵乘法 self.W.T表示把W矩阵转置***）

每个输出都是所有输入的加权和

| 符号 | 含义                                                         |
| ---- | ------------------------------------------------------------ |
| x    | 输入数据（batch_size × in_features）                         |
| W    | 模型参数（权重矩阵，out_features × in_features，随机初始化，可训练） |
| W.T  | W 的转置，用来对齐矩阵乘法（x 的列数必须等于 W.T 的行数）    |
| b    | 偏置（每个输出一个偏置，自动广播到 batch 维度）              |
| y    | 输出数据（batch_size × out_features）                        |

对应到pytorch的Linear

### 神经网络

#### 拟合函数

找一个公式，让输入 x 和输出 y 的关系尽可能匹配： y = f ( x )

#### 线性层

将特征值进行加权求和得到初步的预测结果，只能学习到简单的乘、加关系（线性 Linear/卷积）

#### 激活层

曲线关系，让网络可以学习更加复杂的规律（ReLU/激活）

#### 池化层

下采样提取主要特征：

```python
# 如果你有很多房屋特征组合（比如同一片区、不同楼层、不同户型），池化帮你 挑出最能代表房价的特征，忽略微小波动。

# 直观理解：

# 同一片区10套房子，有房价差异：

# [500k, 520k, 510k, 495k]

# 池化（取最大） → 520k，抓到最显著的价格特征

# 池化（取平均） → 506k，平滑价格

# 在深度网络里，卷积提取特征 → 激活增加弯曲能力 → 池化保留关键特征，重复多层，就能学到复杂规律。
```

#### torch.nn

- **通用训练步骤：**

  - 定义一个包含可学习参数的神经网络：

    class继承nn.Module并实现forward函数。

    ```pyth
    class Net(nn.Module):
    # 对于含有可学习参数的层应该把它放在__init__()函数中
        def __init__(self):
            super().__init__()
            self.w = nn.Parameter(torch.tensor(1.0))
    # 不含有可学习参数的层既可以放在__init__()函数中也可以放在forward函数中
        def forward(self, x):
            return self.w * x
    ```

  - 加载用于训练该网络的数据集

  - 进行前向传播，得到网络输出结果，计算损失（输出结果与正确结果的差距）

    **损失函数：**

    torch.nn实现了绝大部分损失函数

    ```pyth
    import torch
    import torch.nn as nn
    
    # 简单损失函数 → 反向传播 → 溯源 → 拿到参数梯度
    # 这就是自定义的Net模型（继承了nn.Module能被pytorch识别）
    class Net(nn.Module):
        def __init__(self):
            super().__init__()
            self.w = nn.Parameter(torch.tensor(1.0))  # 一个可训练参数
    
        def forward(self, x):
            return self.w * x
    
    # 只有直接运行这个文件时，才执行下面的代码
    if __name__ == "__main__":
        x = torch.tensor(2.0)
        y_true = torch.tensor(8.0)
        model = Net()
        # 调用forward函数前向传播
        y_pred = model(x)
        # 损失函数
        loss = nn.MSELoss()(y_pred, y_true)
        # 反向传播
        loss.backward()
    
        print("loss = ", loss.item())
        print("w grad = ", model.w.grad)
        print("w grad item = ", model.w.grad.item())
    ```

    **Tips:** **梯度为正数 参数往小调 梯度为负数 参数往大调，重点关注参数调整后loss的变化**

  - 进行反向传播，更新网络参数

    **优化器：**

  - 训练完毕 保存网络模型

#### nn.Sequential

当你的网络可以看作一个“严格流水线”，一层接一层，就用它；否则写自定义 forward。

### Dataset

核心职责（只有 3 件事）

1. **定义样本总数**
2. **定义如何按 index 取一条样本**
3. **屏蔽数据来源差异（CSV / DB / 文件 / 内存）**

### DataLoader

核心职责

1. **batch 化**
2. **shuffle**
3. **多进程 / 多线程读取**
4. **prefetch**
5. **pin memory（CPU→GPU 加速）**