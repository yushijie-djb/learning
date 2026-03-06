package cn.yushijie.test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

/**
 * @ClassName RagChatAIService
 * @Description RAG Demo
 * @Author yu155
 * @Date 2026/3/4 17:48
 * @Version 1.0.0
 */
public class RagChatAIService {
    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        // 文档加载 metadata本身是一个Map
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\test\\rag");
        // 文本切割
        DocumentSplitter splitter = DocumentSplitters.recursive(100, 20);
        List<TextSegment> textSegments = splitter.split(documents.get(0));
        textSegments.forEach(textSegment -> System.out.println(textSegment.text()));
        // 预处理并存入向量数据库
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

    }
}
