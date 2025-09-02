import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;

class Page {
    private String title;
    private String path;
    private List<String> tags;
    private Date lastModified;
    private List<Page> children;
    private Page parent; // need this for breadcrumb
    
    public Page(String title, String path, List<String> tags, Date lastModified) {
        this.title = title;
        this.path = path;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.lastModified = lastModified;
        this.children = new ArrayList<>();
        this.parent = null;
    }
    
    public Page(String title, String path, List<String> tags) {
        this(title, path, tags, new Date());
    }
    
    // 1. addChild method
    public void addChild(Page child) {
        if (child != null) {
            this.children.add(child);
            child.parent = this;
        }
    }
    
    // 2. getBreadcrumb method - goes up the tree
    public String getBreadcrumb() {
        if (parent == null) {
            return title;
        }
        return parent.getBreadcrumb() + " > " + title;
    }
    
    // 3. searchByTag - recursive search
    public List<Page> searchByTag(String tag) {
        List<Page> result = new ArrayList<>();
        
        if (tags.contains(tag)) {
            result.add(this);
        }
        
        // check all children too
        for (Page child : children) {
            result.addAll(child.searchByTag(tag));
        }
        
        return result;
    }
    
    // 4. getRecentlyModified - check dates
    public List<Page> getRecentlyModified(int days) {
        List<Page> result = new ArrayList<>();
        
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        Date cutoff = Date.from(cutoffDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        if (lastModified.after(cutoff)) {
            result.add(this);
        }
        
        // check children recursively
        for (Page child : children) {
            result.addAll(child.getRecentlyModified(days));
        }
        
        return result;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getPath() {
        return path;
    }
    
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }
    
    public Date getLastModified() {
        return new Date(lastModified.getTime());
    }
    
    public List<Page> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public String toString() {
        return "Page: " + title + " (" + path + ") - Tags: " + tags;
    }
}

public class CMS {
    
    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        
        // create some test pages
        Page home = new Page("Home", "/", Arrays.asList("main", "homepage"), new Date());
        
        cal.add(Calendar.DAY_OF_MONTH, -2);
        Page products = new Page("Products", "/products", Arrays.asList("catalog", "products"), cal.getTime());
        
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Page laptops = new Page("Laptops", "/products/laptops", Arrays.asList("computers", "laptops", "electronics"), cal.getTime());
        
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Page gaming = new Page("Gaming Laptops", "/products/laptops/gaming", Arrays.asList("gaming", "laptops", "high-performance"), cal.getTime());
        
        cal.add(Calendar.DAY_OF_MONTH, -10);
        Page business = new Page("Business Laptops", "/products/laptops/business", Arrays.asList("business", "laptops", "professional"), cal.getTime());
        
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Page services = new Page("Services", "/services", Arrays.asList("support", "services"), cal.getTime());
        
        Page support = new Page("Support", "/services/support", Arrays.asList("help", "support", "technical"), new Date());
        
        // build the tree structure
        home.addChild(products);
        home.addChild(services);
        products.addChild(laptops);
        laptops.addChild(gaming);
        laptops.addChild(business);
        services.addChild(support);
        
        System.out.println("Testing CMS Content Repository Simulator");
        System.out.println("========================================");
        
        // test breadcrumb
        System.out.println("\nBreadcrumb tests:");
        System.out.println("Gaming page: " + gaming.getBreadcrumb());
        System.out.println("Support page: " + support.getBreadcrumb());
        
        // test tag search
        System.out.println("\nSearching for 'laptops' tag:");
        List<Page> laptopPages = home.searchByTag("laptops");
        for (Page page : laptopPages) {
            System.out.println("Found: " + page.getTitle());
        }
        
        System.out.println("\nSearching for 'support' tag:");
        List<Page> supportPages = home.searchByTag("support");
        for (Page page : supportPages) {
            System.out.println("Found: " + page.getTitle());
        }
        
        // test recent pages
        System.out.println("\nPages modified in last 3 days:");
        List<Page> recent = home.getRecentlyModified(3);
        for (Page page : recent) {
            System.out.println(page.getTitle() + " - " + page.getLastModified());
        }
        
        System.out.println("\nPages modified in last 15 days:");
        recent = home.getRecentlyModified(15);
        for (Page page : recent) {
            System.out.println(page.getTitle() + " - " + page.getLastModified());
        }
    }
}