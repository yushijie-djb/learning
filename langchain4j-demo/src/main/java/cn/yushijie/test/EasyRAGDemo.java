package cn.yushijie.test;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.time.Duration;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

/**
 * @ClassName EasyRAGDemo
 * @Description langchain4j easyrag
 * @Author yu155
 * @Date 2026/3/5 16:29
 * @Version 1.0.0
 */
public class EasyRAGDemo {

    private static String baseUrl = "http://10.0.1.253:11434";
    private static String apiKey = "ollama";

    private static final ChatModel CHAT_MODEL = OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName("qwen3.5:9b")
            .logRequests(true)
            .timeout(Duration.ofMinutes(10L))
            .think(false)
            .build();

    /**
     * This example demonstrates how to implement an "Easy RAG" (Retrieval-Augmented Generation) application.
     * By "easy" we mean that we won't dive into all the details about parsing, splitting, embedding, etc.
     * All the "magic" is hidden inside the "langchain4j-easy-rag" module.
     * <p>
     * If you want to learn how to do RAG without the "magic" of an "Easy RAG", see {@link }.
     */

    public static void main(String[] args) {

        // First, let's load documents that we want to use for RAG
        List<Document> documents = loadDocuments("D:\\test\\rag");

        // Second, let's create an assistant that will have access to our documents
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(CHAT_MODEL) // it should use OpenAI LLM
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // it should remember 10 latest messages
                .contentRetriever(createContentRetriever(documents)) // it should have access to our documents
                .tools(new EasyRagTool())
                .build();

        // Lastly, let's start the conversation with the assistant. We can ask questions like:
        // - Can I cancel my reservation?
        // - I had an accident, should I pay extra?
        String userMessage = "我叫什么名字？";
        System.out.println(userMessage);
        String result1 = assistant.chat(userMessage);
        System.out.println(result1);
//        String userMessage2 = "我的职业是什么？";
//        System.out.println(userMessage2);
//        String result2 = assistant.chat(userMessage2);
//        System.out.println(result2);
//        String userMessage3 = "我的好兄弟是谁？他是个什么样的人";
//        System.out.println(userMessage3);
//        String result3 = assistant.chat(userMessage3);
//        System.out.println(result3);
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {

        // Here, we create an empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

}

class EasyRagTool {

    @Tool(name = "findName")
    public String findName() {
        return "鱼世杰";
    }

    @Tool(name = "findAge")
    public Integer findAge() {
        return 27;
    }

}
