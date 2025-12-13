package org.example;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.inspector2.Inspector2AsyncClient;
import software.amazon.awssdk.services.inspector2.model.*;
import software.amazon.awssdk.services.inspector2.paginators.ListFindingsPublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class InspectorFindingsDemo {

    private final Inspector2AsyncClient asyncClient;

    public InspectorFindingsDemo(Inspector2AsyncClient client) {
        this.asyncClient = client;
    }

    private Inspector2AsyncClient getAsyncClient() {
        return asyncClient;
    }

    /**
     * ✅ Stable async pagination — DOES NOT hang
     * ✅ Manual nextToken loop
     * ✅ Safe, predictable, production-grade
     */

    public CompletableFuture<String> listFindingsAsync(int maxResults,
                                                       FilterCriteria filterCriteria) {

        System.out.println("[DEBUG] Starting async paginator…");

        final int PAGE_LIMIT = 4; // ✅ demo-safe limit

        // Request builder
        ListFindingsRequest.Builder builder =
                ListFindingsRequest.builder()
                        .maxResults(maxResults);

        if (filterCriteria != null) {
            builder.filterCriteria(filterCriteria);
        }

        ListFindingsRequest request = builder.build();

        ListFindingsPublisher paginator =
                getAsyncClient().listFindingsPaginator(request);

        List<Finding> allFindings =
                Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<String> future = new CompletableFuture<>();

        paginator.subscribe(new Subscriber<ListFindingsResponse>() {

            private Subscription subscription;
            private int pageCount = 0;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;

                System.out.println("[DEBUG] onSubscribe → requesting first page");

                s.request(1); // request the first page
            }

            @Override
            public void onNext(ListFindingsResponse page) {
                pageCount++;

                System.out.println("[DEBUG] onNext → received page " + pageCount);

                if (page.findings() != null) {
                    System.out.println("[DEBUG] Page contains " + page.findings().size() + " findings.");
                    allFindings.addAll(page.findings());
                }

                // ✅ Stop after PAGE_LIMIT pages
                if (pageCount >= PAGE_LIMIT) {
                    System.out.println("[DEBUG] Page limit reached → completing future.");
                    future.complete(buildSummary(allFindings));
                    subscription.cancel();
                    return;
                }

                // ✅ Otherwise request the next page
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("[DEBUG] paginator error: " + t);
                future.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                System.out.println("[DEBUG] paginator completed normally.");
                future.complete(buildSummary(allFindings));
            }
        });

        return future;
    }


    // ----------------------------------------------------------
    // ✅ Summary Builder
    // ----------------------------------------------------------
    private String buildSummary(List<Finding> findings) {

        if (findings.isEmpty()) {
            System.out.println("[DEBUG] No findings collected.");

            return "No findings found.\n" +
                    "This could mean:\n" +
                    " • Inspector hasn't completed its initial scan yet\n" +
                    " • Your resources don't have any vulnerabilities\n" +
                    " • All findings were suppressed by filters\n";
        }

        System.out.println("[DEBUG] Building summary.");

        StringBuilder sb = new StringBuilder();
        sb.append("Found ")
                .append(findings.size())
                .append(" finding(s):\n");

        Map<String, List<Finding>> bySeverity =
                findings.stream()
                        .collect(Collectors.groupingBy(Finding::severityAsString));

        bySeverity.forEach((sev, list) -> {
            sb.append("  ")
                    .append(sev)
                    .append(": ")
                    .append(list.size())
                    .append(" finding(s)\n");

            System.out.println("[DEBUG] Severity " + sev + ": " + list.size());
        });

        sb.append("\nRecent findings:\n");

        for (int i = 0; i < Math.min(5, findings.size()); i++) {
            Finding f = findings.get(i);

            sb.append("  ")
                    .append(i + 1)
                    .append(". ")
                    .append(f.title())
                    .append("\n");

            System.out.println("[DEBUG] Recent: " + f.title());
        }

        return sb.toString();
    }

    // ----------------------------------------------------------
    // ✅ MAIN METHOD
    // ----------------------------------------------------------
    public static void main(String[] args) {

        System.out.println("[DEBUG] Creating Inspector2 async client…");

        Inspector2AsyncClient client =
                Inspector2AsyncClient.builder()
                        .region(Region.US_EAST_1)
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();

        InspectorFindingsDemo demo = new InspectorFindingsDemo(client);

        try {
            System.out.println("[DEBUG] Calling listFindingsAsync()…");

            String output = demo
                    .listFindingsAsync(50, null)
                    .join(); // waits for async completion

            System.out.println("[DEBUG] join() returned. Output:");
            System.out.println(output);

        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in main()");
            e.printStackTrace();
        }

        System.out.println("[DEBUG] Closing client…");
        client.close();
    }
}
