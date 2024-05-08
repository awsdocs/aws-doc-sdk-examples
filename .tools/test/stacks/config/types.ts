import * as fs from 'fs';
import { parse } from "yaml";

interface AccountConfig {
    account_id: string;
    status: 'enabled' | 'disabled';
    vcpus?: string;
    memory?: string;
}

interface AccountConfigs {
    [key: string]: AccountConfig;
}

export function readAccountConfig(filePath: string): AccountConfigs {
    try {
        const fileContents = fs.readFileSync(filePath, 'utf8');
        const data: AccountConfigs = parse(fileContents);

        Object.values(data).forEach(config => {
            if (!config.account_id || !config.status) {
                throw new Error("Validation failed: Missing required account fields.");
            }
        });

        return data;
    } catch (error) {
        console.error('Failed to read or parse the YAML file:', error);
        throw error;
    }
}
